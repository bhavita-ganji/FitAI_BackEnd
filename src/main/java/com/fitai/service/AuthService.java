package com.fitai.service;

import com.fitai.dto.AuthRequest;
import com.fitai.dto.AuthResponse;
import com.fitai.dto.RegisterRequest;
import com.fitai.entity.NutritionGoal;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.entity.UserProfile;
import com.fitai.exception.BadRequestException;
import com.fitai.repository.NutritionGoalRepository;
import com.fitai.repository.UserProfileRepository;
import com.fitai.repository.UserRepository;
import com.fitai.security.JwtTokenProvider;
import com.fitai.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final NutritionGoalRepository nutritionGoalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       NutritionGoalRepository nutritionGoalRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.nutritionGoalRepository = nutritionGoalRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Create default empty profile and default nutrition targets
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        userProfileRepository.save(profile);

        NutritionGoal nutritionGoal = new NutritionGoal();
        nutritionGoal.setUser(savedUser);
        nutritionGoalRepository.save(nutritionGoal);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String token = tokenProvider.generateToken(principal);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                false
        );
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(principal);

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BadRequestException("User record not found"));

        boolean hasCompletedOnboarding = user.getProfile() != null &&
                user.getProfile().getAge() != null &&
                user.getProfile().getHeightCm() != null &&
                user.getProfile().getWeightKg() != null;

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                hasCompletedOnboarding
        );
    }
}
