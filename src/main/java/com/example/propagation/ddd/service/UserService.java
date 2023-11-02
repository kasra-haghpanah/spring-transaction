package com.example.propagation.ddd.service;

import com.example.propagation.ddd.dto.SupportDTO;
import com.example.propagation.ddd.model.Customer;
import com.example.propagation.ddd.model.User;
import com.example.propagation.ddd.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(value = "propagationTM", propagation = Propagation.REQUIRED)
    public User save(User user) {
        User user1 = userRepository.save(user);
        return user1;
    }

}
