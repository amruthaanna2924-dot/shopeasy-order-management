package com.shopeasy.ordermanagement.specification;


import com.shopeasy.ordermanagement.entity.Order;
import com.shopeasy.ordermanagement.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }
}
