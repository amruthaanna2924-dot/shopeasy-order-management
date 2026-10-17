package com.shopeasy.ordermanagement.exception;


public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id : " + customerId);
    }

}
