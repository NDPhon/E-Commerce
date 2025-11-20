package com.HCMUS.PHON.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HCMUS.PHON.backend.model.Cart;
import com.HCMUS.PHON.backend.model.CartItem;
import com.HCMUS.PHON.backend.model.Order;
import com.HCMUS.PHON.backend.model.OrderItem;
import com.HCMUS.PHON.backend.model.Products;
import com.HCMUS.PHON.backend.model.Users;
import com.HCMUS.PHON.backend.repository.CartRepo;
import com.HCMUS.PHON.backend.repository.OrderRepo;
import com.HCMUS.PHON.backend.repository.ProductRepo;
import com.HCMUS.PHON.backend.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class OrderService {
    @Autowired private CartRepo cartRepo;
    @Autowired private OrderRepo orderRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private ProductRepo productRepo;

    @Transactional
    public Optional<Order> createOrder(Long userId, String phoneNumber, String address, String paymentMethod){
        Optional<Users> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()){
            return Optional.empty();
        }
        Users user = userOptional.get();
        
        Optional<Cart> cartOptional = cartRepo.findByUser(user);
        if (cartOptional.isEmpty()){
            return Optional.empty();
        }
        Cart cart = cartOptional.get();

        Order order = new Order();
        order.setUser(user);
        order.setPhoneNumber(phoneNumber);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");
        order.setTotalAmount(cart.getTotalAmount());

        for (CartItem cartItem : cart.getItems()){
            Products product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()){
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepo.save(product);
            
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            order.getItems().add(orderItem);
        }
        Order savOrder = orderRepo.save(order);

        cart.getItems().clear();
        cart.setTotalAmount(0);
        cartRepo.save(cart);

        return Optional.of(savOrder);
    }

    public List<Order> getOrdersByUser(String username) {
        Users user = userRepo.findByUsername(username);

        if (user == null){
            throw new RuntimeException("User not found");
        }

        return orderRepo.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
}
