package com.shopeasy.ordermanagement.dto.response;


import com.shopeasy.ordermanagement.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long orderId;

    private String customerName;

    private String productName;

    private Integer quantity;

    private Double pricePerUnit;

    private Double totalAmount;

    private LocalDateTime orderDate;

    private OrderStatus status;

}
