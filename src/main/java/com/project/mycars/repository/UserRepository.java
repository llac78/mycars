package com.project.mycars.repository;

import com.project.mycars.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
}
