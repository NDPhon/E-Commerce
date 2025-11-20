package com.HCMUS.PHON.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.HCMUS.PHON.backend.dto.ApiResponseDTO;
import com.HCMUS.PHON.backend.dto.OrderItemRequest;
import com.HCMUS.PHON.backend.model.Order;
import com.HCMUS.PHON.backend.model.Users;
import com.HCMUS.PHON.backend.repository.UserRepo;
import com.HCMUS.PHON.backend.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepo userRepo;

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponseDTO<>(code, message, data));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<Order>> createOrder(@RequestBody OrderItemRequest orderItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = userRepo.findByUsername(username);
        if (user == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
        }

        Optional<Order> orderOpt = orderService.createOrder(
                user.getId(),
                orderItemRequest.getPhoneNumber(),
                orderItemRequest.getAddress(),
                orderItemRequest.getPaymentMethod()
        );

        if (orderOpt.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST.value(), "Can't create order", null);
        }

        return buildResponse(HttpStatus.OK.value(), "Order created successfully", orderOpt.get());
    }

    @GetMapping("/my/ordering")
    public ResponseEntity<ApiResponseDTO<List<Order>>> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Order> orders = orderService.getOrdersByUser(username);
        return buildResponse(HttpStatus.OK.value(), "Success", orders);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return buildResponse(HttpStatus.OK.value(), "Success", orders);
    }
}
