package com.codingdeparture.auth.controller;

import com.codingdeparture.auth.dto.*;
import com.codingdeparture.auth.entity.User;
import com.codingdeparture.auth.service.AuthService;
import com.codingdeparture.auth.util.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("REST Endpoint Hit: POST /auth/send-otp | Email: {}", email);
        
        String response = authService.sendOtp(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String username = request.get("username");
        String otp = request.get("otp");
        String password = request.get("password"); 
        
        log.info("REST Endpoint Hit: POST /auth/verify-otp | Email: {}", email);
        
        String result = authService.verifyOtp(email, username, otp, password);
        
        String token = jwtUtils.generateToken(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", result);
        response.put("token", token);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
        try {
            User user = authService.authenticateUser(authRequest.getUsername(), authRequest.getPassword());

            String token = jwtUtils.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("token", token);

            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}