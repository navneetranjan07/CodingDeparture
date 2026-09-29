package com.codingdeparture.auth.service;

import com.codingdeparture.auth.entity.User;
import com.codingdeparture.auth.repository.UserRepository;
import com.codingdeparture.auth.util.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String sendOtp(String email) {
        log.info("Received request to generate OTP for email: {}", email);
        
        String otp = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        log.debug("OTP successfully cached in Redis for email: {}", email);

        emailService.sendOtpEmail(email, otp);

        return "OTP sent successfully to " + email;
    }

    public String verifyOtp(String email, String username, String otp, String rawPassword) {
        log.info("Attempting OTP verification for email: {}", email);
        
        String cachedOtp = redisTemplate.opsForValue().get("OTP:" + email);
        log.info("DEBUG -> Entered OTP: [{}], Redis Cached OTP: [{}]", otp, cachedOtp);  

        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            log.warn("OTP verification failed: Invalid or expired OTP for email: {}", email);
            throw new RuntimeException("Invalid or expired OTP!");
        }

        redisTemplate.delete("OTP:" + email);
        log.debug("Cleared OTP from Redis cache for email: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.info("New user registration detected for email: {}", email);
            
            String encodedPassword = (rawPassword != null && !rawPassword.trim().isEmpty()) 
                    ? passwordEncoder.encode(rawPassword) 
                    : passwordEncoder.encode("Default@123");

            user = User.builder()
                    .email(email)
                    .username(username != null && !username.trim().isEmpty() ? username : "Coder_" + new Random().nextInt(9999))
                    .password(encodedPassword)
                    .build();
        } else {
            if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }
            if (username != null && !username.trim().isEmpty()) {
                user.setUsername(username);
            }
        }

        userRepository.save(user);

        log.info("User successfully verified/registered: ID={}, Email={}", user.getId(), user.getEmail());
        return "Registration/Verification successful! Welcome " + user.getUsername();
    }

    public User authenticateUser(String emailOrUsername, String rawPassword) {
        log.info("Attempting login for: {}", emailOrUsername);
        
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new RuntimeException("User not found!")));

        if (user.getPassword() == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("Invalid password for user: {}", emailOrUsername);
            throw new RuntimeException("Invalid username or password!");
        }

        log.info("User logged in successfully with password: {}", user.getEmail());
        return user;
    }
}