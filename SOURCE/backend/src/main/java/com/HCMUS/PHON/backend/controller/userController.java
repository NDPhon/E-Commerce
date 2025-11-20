package com.HCMUS.PHON.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.HCMUS.PHON.backend.dto.ApiResponseDTO;
import com.HCMUS.PHON.backend.model.Users;
import com.HCMUS.PHON.backend.dto.TokenDTO;
import com.HCMUS.PHON.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponseDTO<>(code, message, data));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<Users>> register(@RequestBody Users user) {
        Users createdUser = service.createUser(user);
        if (createdUser != null) {
            return buildResponse(HttpStatus.CREATED.value(), "User registered successfully", createdUser);
        } else {
            return buildResponse(HttpStatus.BAD_REQUEST.value(), "Registration failed", null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<TokenDTO>> login(@RequestBody Users user) {
        String token = service.verify(user);
        if (token != null) {
            return buildResponse(HttpStatus.OK.value(), "Login successful", new TokenDTO(token));
        } else {
            return buildResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password", null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<Iterable<Users>>> getAllUsers() {
        Iterable<Users> users = service.getAllUsers();
        return buildResponse(HttpStatus.OK.value(), "Success", users);
    }

    @GetMapping("/test-role")
    public ResponseEntity<ApiResponseDTO<Object>> testRole(Authentication authentication) {
        return buildResponse(HttpStatus.OK.value(), "Success", authentication.getAuthorities());
    }
}
