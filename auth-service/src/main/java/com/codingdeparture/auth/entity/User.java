package com.codingdeparture.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String referralCode;

	private int globalXp = 0;

	private LocalDateTime createdAt;

	@Column(nullable = false)
	private String password;

	public User() {
	}

	public User(Long id, String email, String username, String referralCode, int globalXp, LocalDateTime createdAt, String password) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.referralCode = referralCode;
		this.globalXp = globalXp;
		this.createdAt = createdAt;
		this.password = password;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		if (this.referralCode == null) {
			this.referralCode = "NAV-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public int getGlobalXp() {
		return globalXp;
	}

	public void setGlobalXp(int globalXp) {
		this.globalXp = globalXp;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static UserBuilder builder() {
		return new UserBuilder();
	}

	public static class UserBuilder {
		private Long id;
		private String email;
		private String username;
		private String referralCode;
		private int globalXp;
		private LocalDateTime createdAt;
		private String password;  

		public UserBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}

		public UserBuilder username(String username) {
			this.username = username;
			return this;
		}

		public UserBuilder referralCode(String referralCode) {
			this.referralCode = referralCode;
			return this;
		}

		public UserBuilder globalXp(int globalXp) {
			this.globalXp = globalXp;
			return this;
		}

		public UserBuilder createdAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public UserBuilder password(String password) {
			this.password = password; 
			return this;
		}

		public User build() {
			return new User(id, email, username, referralCode, globalXp, createdAt, password);
		}
	}
}