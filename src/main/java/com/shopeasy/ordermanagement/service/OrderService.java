package com.shopeasy.ordermanagement.service;





import com.shopeasy.ordermanagement.dto.request.OrderRequestDTO;
import com.shopeasy.ordermanagement.dto.request.UpdateOrderStatusRequestDTO;
import com.shopeasy.ordermanagement.dto.response.OrderPageResponseDTO;
import com.shopeasy.ordermanagement.dto.response.OrderResponseDTO;
import com.shopeasy.ordermanagement.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    /*Order placeOrder(OrderRequestDTO orderRequestDTO);

    List<Order> getAllOrders();

    Order getOrderById(Long id);*/

    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);

   // List<OrderResponseDTO> getAllOrders();
   //OrderPageResponseDTO getAllOrders(int page, int size);
   OrderPageResponseDTO getAllOrders(
           int page,
           int size,
           String sortBy,
           String direction,
           OrderStatus status
   );
    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO cancelOrder(Long orderId);

    //OrderResponseDTO confirmOrder(Long orderId);

    //OrderResponseDTO packOrder(Long orderId);

    //OrderResponseDTO shipOrder(Long orderId);

    //OrderResponseDTO deliverOrder(Long orderId);
    OrderResponseDTO updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDTO request
    );
}
