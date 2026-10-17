package com.shopeasy.ordermanagement.service;

import com.shopeasy.ordermanagement.entity.Customer;
import com.shopeasy.ordermanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repository;

    @Override
    public Customer createCustomer(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id : " + id));

    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {

        Customer existingCustomer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id : " + id));

        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());

        return repository.save(existingCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer existingCustomer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id : " + id));

        repository.delete(existingCustomer);

    }
}