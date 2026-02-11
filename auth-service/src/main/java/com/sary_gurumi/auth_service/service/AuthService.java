package com.sary_gurumi.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sary_gurumi.auth_service.entity.User;
import com.sary_gurumi.auth_service.entity.UserStatus;
import com.sary_gurumi.auth_service.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public User register(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CLIENT");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User registerAdmin(User user) {
        user.setRole("ADMIN");
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void recordFailedLoginAttempt(String email) {
        User user = userRepository.findById(email).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            user.setLastFailedLogin(LocalDateTime.now());
            if (user.getFailedLoginAttempts() >= 3) {
                user.setStatus(UserStatus.BLOCKED);
            }
            userRepository.save(user);
        }
    }

    public void recordSuccessfulLogin(String email) {
        User user = userRepository.findById(email).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(0);
            user.setLastFailedLogin(null);
            userRepository.save(user);
        }
    }

    public User suspendAccount(String email) {
        User user = userRepository.findById(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.SUSPENDED);
        return userRepository.save(user);
    }

    public User activateAccount(String email) {
        User user = userRepository.findById(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLastFailedLogin(null);
        return userRepository.save(user);
    }

    public User unblockAccount(String email) {
        User user = userRepository.findById(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLastFailedLogin(null);
        return userRepository.save(user);
    }
}