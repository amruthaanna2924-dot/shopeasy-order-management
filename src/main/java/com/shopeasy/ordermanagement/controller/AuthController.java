package com.shopeasy.ordermanagement.controller;


import com.shopeasy.ordermanagement.dto.request.LoginRequestDTO;
import com.shopeasy.ordermanagement.dto.request.RegisterRequestDTO;
import com.shopeasy.ordermanagement.dto.response.AuthResponseDTO;
import com.shopeasy.ordermanagement.entity.User;
import com.shopeasy.ordermanagement.repository.UserRepository;
import com.shopeasy.ordermanagement.security.CustomUserDetailsService;
import com.shopeasy.ordermanagement.security.JwtService;
import com.shopeasy.ordermanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {

        AuthResponseDTO response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token =
                jwtService.generateToken(userDetails);

        User user =
                userRepository
                        .findByUsername(request.getUsername())
                        .orElseThrow();

        AuthResponseDTO response =
                AuthResponseDTO.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .userId(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .build();

        return ResponseEntity.ok(response);
    }
}
