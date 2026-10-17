package com.shopeasy.ordermanagement.service;

import com.shopeasy.ordermanagement.dto.request.OrderRequestDTO;
import com.shopeasy.ordermanagement.dto.request.UpdateOrderStatusRequestDTO;
import com.shopeasy.ordermanagement.dto.response.OrderResponseDTO;
import com.shopeasy.ordermanagement.entity.Customer;
import com.shopeasy.ordermanagement.dto.response.OrderPageResponseDTO;
import com.shopeasy.ordermanagement.entity.Order;
import com.shopeasy.ordermanagement.entity.Product;
import com.shopeasy.ordermanagement.enums.OrderStatus;
import com.shopeasy.ordermanagement.exception.*;
import com.shopeasy.ordermanagement.repository.CustomerRepository;
import com.shopeasy.ordermanagement.repository.OrderRepository;
import com.shopeasy.ordermanagement.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.shopeasy.ordermanagement.specification.OrderSpecification;
import org.springframework.data.jpa.domain.Specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {

        log.info("Placing order for customerId={}, productId={}, quantity={}",
                orderRequestDTO.getCustomerId(),
                orderRequestDTO.getProductId(),
                orderRequestDTO.getQuantity());

        // Find Customer
        Customer customer = customerRepository.findById(orderRequestDTO.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Customer not found. customerId={}",
                            orderRequestDTO.getCustomerId());
                    return new CustomerNotFoundException(
                            orderRequestDTO.getCustomerId());
                });

        // Find Product
        Product product = productRepository.findById(orderRequestDTO.getProductId())
                .orElseThrow(() -> {
                    log.warn("Product not found. productId={}",
                            orderRequestDTO.getProductId());
                    return new ProductNotFoundException(
                            orderRequestDTO.getProductId());
                });

        // Validate Quantity
        if (orderRequestDTO.getQuantity() <= 0) {
            log.warn("Invalid order quantity. quantity={}",
                    orderRequestDTO.getQuantity());

            throw new RuntimeException("Quantity should be greater than zero");
        }

        // Check Stock
        if (product.getStock() < orderRequestDTO.getQuantity()) {

            log.warn("Insufficient stock. productId={}, requestedQuantity={}, availableStock={}",
                    product.getId(),
                    orderRequestDTO.getQuantity(),
                    product.getStock());

            throw new InsufficientStockException(
                    "Insufficient stock available");
        }

        // Calculate Total Amount
        Double totalAmount =
                product.getPrice() * orderRequestDTO.getQuantity();

        log.debug("Order total calculated. productId={}, quantity={}, totalAmount={}",
                product.getId(),
                orderRequestDTO.getQuantity(),
                totalAmount);

        // Reduce Stock
        product.setStock(
                product.getStock() - orderRequestDTO.getQuantity());

        // Save Updated Product
        productRepository.save(product);

        log.debug("Product stock updated. productId={}, remainingStock={}",
                product.getId(),
                product.getStock());

        // Create Order
        Order order = Order.builder()
                .customer(customer)
                .product(product)
                .quantity(orderRequestDTO.getQuantity())
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .build();

        // Save Order
        Order savedOrder = orderRepository.save(order);

        log.info("Order placed successfully. orderId={}, customerId={}, productId={}, status={}",
                savedOrder.getId(),
                customer.getId(),
                product.getId(),
                savedOrder.getStatus());

        return mapToDTO(savedOrder);
    }

    @Override
    public OrderPageResponseDTO getAllOrders(
            int page,
            int size,
            String sortBy,
            String direction,
            OrderStatus status) {

        log.info("Fetching orders. page={}, size={}, sortBy={}, direction={}, status={}",
                page, size, sortBy, direction, status);

        Sort.Direction sortDirection =
                Sort.Direction.fromString(direction);

        Sort sort =
                Sort.by(sortDirection, sortBy);

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<Order> specification = null;

        if (status != null) {
            log.debug("Applying order status filter. status={}", status);

            specification =
                    OrderSpecification.hasStatus(status);
        }

        Page<Order> orderPage =
                orderRepository.findAll(
                        specification,
                        pageable
                );

        List<OrderResponseDTO> orderDTOs =
                orderPage.getContent()
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        log.info("Orders fetched successfully. returnedOrders={}, totalElements={}, totalPages={}",
                orderDTOs.size(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages());

        return OrderPageResponseDTO.builder()
                .content(orderDTOs)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {

        log.info("Fetching order by id. orderId={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found. orderId={}", id);
                    return new OrderNotFoundException(id);
                });

        log.info("Order fetched successfully. orderId={}, status={}",
                order.getId(),
                order.getStatus());

        return mapToDTO(order);
    }

    private OrderResponseDTO mapToDTO(Order order) {

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .customerName(order.getCustomer().getName())
                .productName(order.getProduct().getName())
                .quantity(order.getQuantity())
                .pricePerUnit(order.getProduct().getPrice())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }

    @Override
    public OrderResponseDTO cancelOrder(Long orderId) {

        log.info("Cancelling order. orderId={}", orderId);

        // Find the Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found while cancelling. orderId={}",
                            orderId);
                    return new OrderNotFoundException(orderId);
                });

        // Check if order is already cancelled
        if (order.getStatus() == OrderStatus.CANCELLED) {

            log.warn("Order is already cancelled. orderId={}", orderId);

            throw new OrderAlreadyCancelledException(orderId);
        }

        // Check if delivered
        if (order.getStatus() == OrderStatus.DELIVERED) {

            log.warn("Attempt to cancel delivered order. orderId={}",
                    orderId);

            throw new InvalidOrderStatusException(
                    "Delivered orders cannot be cancelled.");
        }

        // Get the product from the order
        Product product = order.getProduct();

        // Restore the stock
        product.setStock(
                product.getStock() + order.getQuantity());

        // Save the Product
        productRepository.save(product);

        log.debug("Stock restored after order cancellation. productId={}, restoredQuantity={}, currentStock={}",
                product.getId(),
                order.getQuantity(),
                product.getStock());

        // Update the Order Status
        order.setStatus(OrderStatus.CANCELLED);

        // Save the Order
        Order cancelledOrder = orderRepository.save(order);

        log.info("Order cancelled successfully. orderId={}, status={}",
                cancelledOrder.getId(),
                cancelledOrder.getStatus());

        return mapToDTO(cancelledOrder);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDTO request) {

        log.info("Updating order status. orderId={}, requestedStatus={}",
                orderId,
                request.getStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found while updating status. orderId={}",
                            orderId);
                    return new OrderNotFoundException(orderId);
                });

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        log.debug("Current order status. orderId={}, currentStatus={}, newStatus={}",
                orderId,
                currentStatus,
                newStatus);

        if (currentStatus == OrderStatus.CANCELLED) {

            log.warn("Attempt to update cancelled order. orderId={}",
                    orderId);

            throw new InvalidOrderStatusException(
                    "Cancelled orders cannot be updated."
            );
        }

        if (currentStatus == OrderStatus.DELIVERED) {

            log.warn("Attempt to update delivered order. orderId={}",
                    orderId);

            throw new InvalidOrderStatusException(
                    "Delivered orders cannot be updated."
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {

            log.warn("Attempt to cancel order using status update operation. orderId={}",
                    orderId);

            throw new InvalidOrderStatusException(
                    "Use the cancel order operation to cancel an order."
            );
        }

        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated successfully. orderId={}, oldStatus={}, newStatus={}",
                orderId,
                currentStatus,
                updatedOrder.getStatus());

        return mapToDTO(updatedOrder);
    }
}
