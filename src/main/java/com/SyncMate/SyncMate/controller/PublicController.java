package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.LoginRequest;
import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.services.JwtService;
import com.SyncMate.SyncMate.services.UserDetailsServiceImpl;
import com.SyncMate.SyncMate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    private ResponseEntity<String> registerUser(@RequestBody RegisterRequest user){
        userService.saveNewUser(user);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateAndGetToken(@RequestBody LoginRequest authRequest) {
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );
        if (authentication.isAuthenticated()) {
            String newAccessToken = jwtService.generateAccessToken(email);
            String newRefreshToken = jwtService.generateRefreshToken(email);

            TokenResponse tokenResponse = new TokenResponse(newAccessToken,newRefreshToken);
            return ResponseEntity.ok(tokenResponse);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refresh_token");
        String email = jwtService.extractUsername(refreshToken);

        if (email != null && jwtService.validateRefreshToken(refreshToken,userDetailsService.loadUserByUsername(email) )) {
            String newAccessToken = jwtService.generateAccessToken(email);
            return ResponseEntity.ok(new TokenResponse(newAccessToken, null));
        }
        throw new UsernameNotFoundException("Invalid refresh token!");
    }
}
