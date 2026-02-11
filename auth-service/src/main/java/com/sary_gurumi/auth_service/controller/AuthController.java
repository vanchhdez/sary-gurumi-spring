package com.sary_gurumi.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sary_gurumi.auth_service.AuthRequest;
import com.sary_gurumi.auth_service.entity.User;
import com.sary_gurumi.auth_service.repository.UserRepository;
import com.sary_gurumi.auth_service.service.AuthService;
import com.sary_gurumi.auth_service.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService; // Tu lógica de validar usuario en DB

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String getToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                User user = userRepository.findById(authRequest.getEmail()).orElseThrow();
                authService.recordSuccessfulLogin(authRequest.getEmail());
                return jwtService.generateToken(authRequest.getEmail(), user.getRole());
            } else {
                authService.recordFailedLoginAttempt(authRequest.getEmail());
                throw new RuntimeException("Acceso inválido");
            }
        } catch (Exception e) {
            authService.recordFailedLoginAttempt(authRequest.getEmail());
            throw new RuntimeException("Acceso inválido");
        }
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public User registerAdmin(@RequestBody User user) {
        return authService.registerAdmin(user);
    }

    @PutMapping("/suspend")
    @PreAuthorize("isAuthenticated()")
    public User suspendAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authService.suspendAccount(email);
    }

    @PutMapping("/activate")
    @PreAuthorize("isAuthenticated()")
    public User activateAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authService.activateAccount(email);
    }

    @PutMapping("/unblock/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public User unblockAccount(@PathVariable String email) {
        return authService.unblockAccount(email);
    }
}