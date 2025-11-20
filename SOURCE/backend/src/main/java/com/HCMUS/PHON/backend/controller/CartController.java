package com.HCMUS.PHON.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.HCMUS.PHON.backend.dto.AddItemRequest;
import com.HCMUS.PHON.backend.dto.DeleteItemRequest;
import com.HCMUS.PHON.backend.dto.UpdateQuantityRequest;
import com.HCMUS.PHON.backend.dto.ApiResponseDTO;
import com.HCMUS.PHON.backend.model.Cart;
import com.HCMUS.PHON.backend.model.Users;
import com.HCMUS.PHON.backend.repository.UserRepo;
import com.HCMUS.PHON.backend.service.CartService;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepo userRepo;

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponseDTO<>(code, message, data));
    }

    @GetMapping("/item")
    public ResponseEntity<ApiResponseDTO<Cart>> getUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Users user = userRepo.findByUsername(name);

        if (user == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
        }

        Optional<Cart> cartOpt = cartService.getCartByUserId(user.getId());
        if (cartOpt.isEmpty()) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "Cart not found", null);
        }

        return buildResponse(HttpStatus.OK.value(), "Success", cartOpt.get());
    }

    @PostMapping("/add/item")
    public ResponseEntity<ApiResponseDTO<Cart>> addItemToCart(@RequestBody AddItemRequest addItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Users user = userRepo.findByUsername(name);

        if (user == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
        }

        Optional<Cart> cartOpt = cartService.addItemToCart(user.getId(), addItemRequest.getProductId(), addItemRequest.getQuantity());
        if (cartOpt.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST.value(), "Can't add this item", null);
        }

        return buildResponse(HttpStatus.OK.value(), "Item added", cartOpt.get());
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<ApiResponseDTO<Object>> deleteItem(@RequestBody DeleteItemRequest deleteItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Users user = userRepo.findByUsername(name);

        if (user == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
        }

        cartService.deleteItem(user.getId(), deleteItemRequest.getProductId());
        return buildResponse(HttpStatus.OK.value(), "Item deleted", null);
    }

    @PutMapping("/update/quantity")
    public ResponseEntity<ApiResponseDTO<Object>> updateQuantity(@RequestBody UpdateQuantityRequest updateQuantityRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Users user = userRepo.findByUsername(name);

        if (user == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
        }

        cartService.updateQuantity(user.getId(), updateQuantityRequest.getProductId(), updateQuantityRequest.getQuantity());
        return buildResponse(HttpStatus.OK.value(), "Item updated", null);
    }
}
