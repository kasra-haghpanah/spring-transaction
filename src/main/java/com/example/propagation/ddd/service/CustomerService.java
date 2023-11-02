package com.example.propagation.ddd.service;

import com.example.propagation.ddd.model.Customer;
import com.example.propagation.ddd.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(value = "propagationTM", propagation = Propagation.REQUIRES_NEW)
    public Customer saveCustomer(Customer customer) {
        Customer customer1 = customerRepository.save(customer);
        if (true) {
            throw new RuntimeException("propagationTM");
        }
        return customer1;
    }

}
