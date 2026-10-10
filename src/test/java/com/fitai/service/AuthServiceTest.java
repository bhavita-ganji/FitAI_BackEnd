package com.fitai.service;

import com.fitai.dto.AuthRequest;
import com.fitai.dto.AuthResponse;
import com.fitai.dto.RegisterRequest;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.exception.BadRequestException;
import com.fitai.repository.NutritionGoalRepository;
import com.fitai.repository.UserProfileRepository;
import com.fitai.repository.UserRepository;
import com.fitai.security.JwtTokenProvider;
import com.fitai.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private NutritionGoalRepository nutritionGoalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("sarah@fitai.com", "encoded_pwd", "Sarah Connor", Role.ROLE_USER);
        sampleUser.setId(10L);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("sarah@fitai.com", "Password@123", "Sarah Connor");

        when(userRepository.existsByEmail("sarah@fitai.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_pwd");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateToken(any(UserPrincipal.class))).thenReturn("jwt_sample_token");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("jwt_sample_token", resp.getToken());
        assertEquals("sarah@fitai.com", resp.getEmail());
        assertEquals(10L, resp.getUserId());
        verify(userProfileRepository, times(1)).save(any());
        verify(nutritionGoalRepository, times(1)).save(any());
    }

    @Test
    void testRegisterDuplicateEmailThrowsBadRequest() {
        RegisterRequest req = new RegisterRequest("sarah@fitai.com", "Password@123", "Sarah Connor");
        when(userRepository.existsByEmail("sarah@fitai.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest("sarah@fitai.com", "Password@123");
        UserPrincipal principal = UserPrincipal.create(sampleUser);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(principal)).thenReturn("valid_jwt");
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("valid_jwt", resp.getToken());
        assertEquals(10L, resp.getUserId());
    }
}
