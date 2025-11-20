package com.HCMUS.PHON.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.HCMUS.PHON.backend.model.Users;
import com.HCMUS.PHON.backend.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    AuthenticationManager authenticationManager;

    public List<Users> getAllUsers(){
        return userRepository.findAll();
    }

    public Users createUser(Users user) {
        Users existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            return null;
        }

        user.setPassword_hashed(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public Users findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public String verify(Users user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getId(), user.getUsername());
        } else {
            return null;
        }
    }

    
}
