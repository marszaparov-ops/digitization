package com.archive.digitization;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Этот метод критически важен для входа
    Optional<User> findByUsername(String username);
}