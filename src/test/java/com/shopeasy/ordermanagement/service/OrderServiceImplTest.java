package com.shopeasy.ordermanagement.service;

import com.shopeasy.ordermanagement.dto.request.OrderRequestDTO;
import com.shopeasy.ordermanagement.dto.response.OrderResponseDTO;
import com.shopeasy.ordermanagement.entity.Order;
import com.shopeasy.ordermanagement.enums.OrderStatus;
import com.shopeasy.ordermanagement.exception.*;
import com.shopeasy.ordermanagement.repository.CustomerRepository;
import com.shopeasy.ordermanagement.repository.OrderRepository;
import com.shopeasy.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopeasy.ordermanagement.entity.Customer;
import com.shopeasy.ordermanagement.entity.Product;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getOrderById_success() {

        Long orderId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setProduct(product);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());

        verify(orderRepository).findById(orderId);
    }
    @Test
    void getOrderById_notFound() {

        Long orderId = 999L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(orderId)
        );

        verify(orderRepository).findById(orderId);
    }

    @Test
    void cancelOrder_success() {

        Long orderId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStock(5);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(2);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Mock the save operation
        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDTO result = orderService.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        assertEquals(7, product.getStock());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }
    @Test
    void cancelOrder_alreadyCancelled() {

        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                OrderAlreadyCancelledException.class,
                () -> orderService.cancelOrder(orderId)
        );

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(order);
    }
    @Test
    void cancelOrder_deliveredOrder() {

        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.cancelOrder(orderId)
        );

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(order);
    }
    @Test
    void placeOrder_success() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(10);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setCustomerId(1L);
        request.setProductId(1L);
        request.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomer(customer);
        savedOrder.setProduct(product);
        savedOrder.setQuantity(2);
        savedOrder.setStatus(OrderStatus.PLACED);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        OrderResponseDTO result = orderService.placeOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("John", result.getCustomerName());
        assertEquals("Laptop", result.getProductName());
        assertEquals(2, result.getQuantity());
        assertEquals(OrderStatus.PLACED, result.getStatus());

        assertEquals(8, product.getStock());

        verify(customerRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_customerNotFound() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setCustomerId(999L);
        request.setProductId(1L);
        request.setQuantity(2);

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> orderService.placeOrder(request)
        );

        verify(customerRepository).findById(999L);
        verify(productRepository, never()).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void placeOrder_productNotFound() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setCustomerId(1L);
        request.setProductId(999L);
        request.setQuantity(2);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.placeOrder(request)
        );

        verify(customerRepository).findById(1L);
        verify(productRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void placeOrder_insufficientStock() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setCustomerId(1L);
        request.setProductId(1L);
        request.setQuantity(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(5);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(request)
        );

        verify(customerRepository).findById(1L);
        verify(productRepository).findById(1L);

        verify(productRepository, never()).save(product);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
