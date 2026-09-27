package com.codingdeparture.auth.service;

import com.codingdeparture.auth.entity.User;
import com.codingdeparture.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    public String sendOtp(String email) {
        log.info("Received request to generate OTP for email: {}", email);
        
        String otp = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        log.debug("OTP successfully cached in Redis for email: {}", email);

        emailService.sendOtpEmail(email, otp);

        return "OTP sent successfully to " + email;
    }

    public String verifyOtp(String email, String username, String otp) {
        log.info("Attempting OTP verification for email: {}", email);
        
        String cachedOtp = redisTemplate.opsForValue().get("OTP:" + email);

        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            log.warn("OTP verification failed: Invalid or expired OTP for email: {}", email);
            throw new RuntimeException("Invalid or expired OTP!");
        }

        redisTemplate.delete("OTP:" + email);
        log.debug("Cleared OTP from Redis cache for email: {}", email);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("New user registration detected for email: {}", email);
            User newUser = User.builder()
                    .email(email)
                    .username(username != null ? username : "Coder_" + new Random().nextInt(9999))
                    .build();
            return userRepository.save(newUser);
        });

        log.info("User successfully logged in: ID={}, Email={}", user.getId(), user.getEmail());
        return "Login successful! Welcome " + user.getUsername();
    }
}