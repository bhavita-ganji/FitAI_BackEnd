package com.fitai.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private boolean hasCompletedOnboarding;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String email, String fullName, String role, boolean hasCompletedOnboarding) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.hasCompletedOnboarding = hasCompletedOnboarding;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isHasCompletedOnboarding() { return hasCompletedOnboarding; }
    public void setHasCompletedOnboarding(boolean hasCompletedOnboarding) { this.hasCompletedOnboarding = hasCompletedOnboarding; }
}
