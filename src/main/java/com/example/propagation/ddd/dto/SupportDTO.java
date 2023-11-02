package com.example.propagation.ddd.dto;

import com.example.propagation.ddd.model.Customer;
import com.example.propagation.ddd.model.User;

public class SupportDTO {

    User user;
    Customer customer;

    public SupportDTO() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
