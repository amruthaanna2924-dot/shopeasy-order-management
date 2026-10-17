package com.shopeasy.ordermanagement.dto.request;



import com.shopeasy.ordermanagement.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
