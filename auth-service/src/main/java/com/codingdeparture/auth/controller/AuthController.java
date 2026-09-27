package com.codingdeparture.auth.controller;

import com.codingdeparture.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("REST Endpoint Hit: POST /auth/send-otp | Email: {}", email);
        
        String response = authService.sendOtp(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String username = request.get("username");
        String otp = request.get("otp");
        
        log.info("REST Endpoint Hit: POST /auth/verify-otp | Email: {}", email);
        
        String result = authService.verifyOtp(email, username, otp);
        return ResponseEntity.ok(result);
    }
}