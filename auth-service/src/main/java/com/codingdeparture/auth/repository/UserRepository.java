package com.codingdeparture.auth.repository;

import com.codingdeparture.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByReferralCode(String referralCode);
	Optional<User> findByUsername(String emailOrUsername);
}