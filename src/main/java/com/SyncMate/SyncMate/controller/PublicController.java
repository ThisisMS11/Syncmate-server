package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.LoginRequest;
import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.authentication.AuthResponse;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
            @ApiResponse(responseCode = "200", description = "Successfully deleted the file", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "409", description = "User Already Exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<MakeResponseDto<?>> registerUser(@Valid @RequestBody RegisterRequest user) {
        userService.saveNewUser(user);
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User registered successfully", null);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Login User", description = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logged in the user",
                    content = @Content(schema = @Schema(implementation = MakeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @PostMapping("/login")
    public ResponseEntity<MakeResponseDto<?>> authenticateAndGetToken(
            @Valid @RequestBody LoginRequest authRequest,
            HttpServletResponse response) {

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

            // Create secure HTTP-only cookies for tokens
            Cookie accessTokenCookie = createSecureCookie("access_token", newAccessToken, 15 * 60); // 15 minutes
            Cookie refreshTokenCookie = createSecureCookie("refresh_token", newRefreshToken, 7 * 24 * 60 * 60); // 7 days

            // Add cookies to response
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Save token configuration (you might want to modify this based on your needs)
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
            userConfigService.saveUserConfig(tokenResponse, email);

            // Return success response without tokens in body
            MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User logged in successfully", null);
            return ResponseEntity.ok(finalResponse);
        } else {
            throw CommonExceptions.invalidRequest("Invalid user request!");
        }
    }


    @Operation(summary = "Regenerate Access Token", description = "Regenerate the access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully regenerated the access token",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuthResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<MakeResponseDto<?>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refresh_token");
        String email = jwtService.extractUsername(refreshToken);

        if (email != null && jwtService.validateRefreshToken(refreshToken, userDetailsService.loadUserByUsername(email))) {
            String newAccessToken = jwtService.generateAccessToken(email);
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, null);
            userConfigService.saveUserConfig(tokenResponse, email);
            MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User registered successfully", tokenResponse);
            return ResponseEntity.ok(finalResponse);
        }

        log.error("User not found, Throwing UsernameNotFoundException Exception");
        throw CommonExceptions.invalidRequest("Invalid refresh token!");
    }


    private Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);           // Prevents XSS attacks
        cookie.setSecure(true);             // Only send over HTTPS (set to false for local development)
        cookie.setPath("/");                // Available for entire application
        cookie.setMaxAge(maxAge);           // Set expiration time
        cookie.setAttribute("SameSite", "Strict"); // CSRF protection
        return cookie;
    }
}
