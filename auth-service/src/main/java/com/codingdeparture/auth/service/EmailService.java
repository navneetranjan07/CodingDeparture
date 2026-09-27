package com.codingdeparture.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        log.info("Preparing to send OTP email to: {}", toEmail);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // Explicitly set from email
            message.setTo(toEmail);
            message.setSubject("Your CodingDeparture Login OTP");
            message.setText("Welcome to CodingDeparture!\n\nYour One-Time Password (OTP) for login is: " + otp + 
                            "\nThis OTP is valid for 5 minutes.\n\nHappy Coding!");
            
            mailSender.send(message);
            log.info("OTP email successfully sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email. Please check your Gmail SMTP configurations.");
        }
    }
}