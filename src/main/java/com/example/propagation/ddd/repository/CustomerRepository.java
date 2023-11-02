package com.example.propagation.ddd.repository;

import com.example.propagation.ddd.model.Customer;
import com.example.propagation.ddd.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
