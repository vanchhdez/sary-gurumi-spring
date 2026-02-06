package com.sary_gurumi.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sary_gurumi.auth_service.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}