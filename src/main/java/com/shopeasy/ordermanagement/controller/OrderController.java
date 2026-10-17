package com.shopeasy.ordermanagement.controller;


import com.shopeasy.ordermanagement.dto.request.OrderRequestDTO;
import com.shopeasy.ordermanagement.dto.request.UpdateOrderStatusRequestDTO;
import com.shopeasy.ordermanagement.dto.response.ApiResponse;
import com.shopeasy.ordermanagement.dto.response.OrderPageResponseDTO;
import com.shopeasy.ordermanagement.dto.response.OrderResponseDTO;
import com.shopeasy.ordermanagement.enums.OrderStatus;
import com.shopeasy.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

 /*   @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {

        return orderService.placeOrder(orderRequestDTO);

    }*/

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(
            @Valid @RequestBody OrderRequestDTO request) {

        OrderResponseDTO response = orderService.placeOrder(request);

        ApiResponse<OrderResponseDTO> apiResponse =
                ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Order placed successfully.")
                        .data(response)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {

        return orderService.getOrderById(id);

    }

    @GetMapping
    public ResponseEntity<ApiResponse<OrderPageResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) OrderStatus status){

        OrderPageResponseDTO orders =
                orderService.getAllOrders(
                        page,
                        size,
                        sortBy,
                        direction,
                        status);

        ApiResponse<OrderPageResponseDTO> response =
                ApiResponse.<OrderPageResponseDTO>builder()
                        .success(true)
                        .message("Orders retrieved successfully.")
                        .data(orders)
                        .build();

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) {

        return orderService.cancelOrder(id);

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request) {

        return orderService.updateOrderStatus(id, request);
    }

}
