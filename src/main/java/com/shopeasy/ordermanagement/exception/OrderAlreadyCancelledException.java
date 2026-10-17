package com.shopeasy.ordermanagement.exception;

public class OrderAlreadyCancelledException extends RuntimeException {

    public OrderAlreadyCancelledException(Long orderId) {
        super("Order with id " + orderId + " is already cancelled.");
    }

}
