package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.LoginRequest;
import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.services.JwtService;
import com.SyncMate.SyncMate.services.UserConfigService;
import com.SyncMate.SyncMate.services.UserDetailsServiceImpl;
import com.SyncMate.SyncMate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
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

    @Autowired
    private UserConfigService userConfigService;

    @Operation(summary = "Register User", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the file", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "409", description = "User Already Exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    private ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest user) {
        userService.saveNewUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "Login User", description = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logged in the user",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateAndGetToken(@Valid @RequestBody LoginRequest authRequest) {
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        if (authentication.isAuthenticated()) {
            // manually updating the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String newAccessToken = jwtService.generateAccessToken(email);
            String newRefreshToken = jwtService.generateRefreshToken(email);
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
            userConfigService.saveUserConfig(tokenResponse, email);
            return ResponseEntity.ok(tokenResponse);
        } else {
            throw CommonExceptions.invalidRequest("Invalid user request!");
        }
    }

    @Operation(summary = "Regenerate Access Token", description = "Regenerate the access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully regenerated the access token",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refresh_token");
        String email = jwtService.extractUsername(refreshToken);

        if (email != null && jwtService.validateRefreshToken(refreshToken, userDetailsService.loadUserByUsername(email))) {
            String newAccessToken = jwtService.generateAccessToken(email);
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, null);
            userConfigService.saveUserConfig(tokenResponse, email);
            return ResponseEntity.ok(tokenResponse);
        }

        log.error("User not found, Throwing UsernameNotFoundException Exception");
        throw CommonExceptions.invalidRequest("Invalid refresh token!");
    }
}
