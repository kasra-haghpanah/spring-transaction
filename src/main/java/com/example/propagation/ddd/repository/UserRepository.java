package com.example.propagation.ddd.repository;

import com.example.propagation.ddd.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
