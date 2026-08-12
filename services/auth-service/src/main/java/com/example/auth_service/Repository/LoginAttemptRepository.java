package com.example.auth_service.Repository;

import com.example.auth_service.Models.LoginAttempt;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface LoginAttemptRepository extends CrudRepository<LoginAttempt, UUID> {
    List<LoginAttempt> findAllByUserIdOrderByAttemptedAtDesc(UUID userId);
    List<LoginAttempt> findAllByIdentifierOrderByAttemptedAtDesc(String identifier);
}
