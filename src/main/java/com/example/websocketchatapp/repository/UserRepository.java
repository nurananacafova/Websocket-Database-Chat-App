package com.example.websocketchatapp.repository;

import com.example.websocketchatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findBySessionId(String sessionId);

    Optional<User> findByName(String name);
}
