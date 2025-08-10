package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.constants.TimeBuckets;
import com.SyncMate.SyncMate.dto.LoginRequest;
import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.dto.UserinfoDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.authentication.LoginResponse;
import com.SyncMate.SyncMate.dto.responses.user.UserInfoResponse;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.Role;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.*;

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

    @Autowired
    private UtilService utilService;


    @Operation(summary = "Register User", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the file", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
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
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
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
            Cookie accessTokenCookie = createSecureCookie("access_token", newAccessToken, TimeBuckets.ACCESS_TOKEN_EXPIRY_TIME); // 15 minutes
            Cookie refreshTokenCookie = createSecureCookie("refresh_token", newRefreshToken, TimeBuckets.REFRESH_TOKEN_EXPIRE_TIME); // 7 days

            // Add cookies to response
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Save token configuration (you might want to modify this based on your needs)
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
            userConfigService.saveUserConfig(tokenResponse, email);

            User user = userService.getUserByEmail(email);

            List<Role> roleList = new ArrayList<>(user.getRoles());
            UserinfoDto userinfoDto = new UserinfoDto(user.getId(), user.getUsername(), user.getEmail(), roleList);

            // Return success response without tokens in body
            MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User logged in successfully", userinfoDto);
            return ResponseEntity.ok(finalResponse);
        } else {
            throw CommonExceptions.invalidRequest("Invalid user request!");
        }
    }


    @Operation(summary = "Regenerate Access Token", description = "Regenerate the access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully regenerated the access token",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoginResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<MakeResponseDto<?>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        log.info("Starting refreshToken endpoint");

        String refreshToken = null;

        if (refreshTokenRequest != null) {
            refreshToken = refreshTokenRequest.get("refresh_token");
            log.info("Refresh token received from request body");
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.info("Refresh token not found in request body, checking cookies");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> authCookie = Arrays.stream(cookies)
                        .filter(c -> "refresh_token".equals(c.getName()))
                        .findFirst();
                if (authCookie.isPresent()) {
                    refreshToken = authCookie.get().getValue();
                    log.info("Refresh token retrieved from cookies");
                } else {
                    log.error("Refresh token not found in cookies");
                    throw CommonExceptions.invalidRequest("Refresh Token Not found");
                }
            } else {
                log.error("No cookies found in request");
                throw CommonExceptions.invalidRequest("Refresh Token Not found");
            }
        }

        log.info("Extracting email from refresh token");
        String email = jwtService.extractUsername(refreshToken);

        if (email != null && jwtService.validateRefreshToken(refreshToken, userDetailsService.loadUserByUsername(email))) {
            log.info("Refresh token is valid for user: {}", email);

            String newAccessToken = jwtService.generateAccessToken(email);
            log.info("New access token generated");

            TokenResponse tokenResponse = new TokenResponse(newAccessToken, null);

            Cookie accessTokenCookie = createSecureCookie("access_token", newAccessToken, TimeBuckets.ACCESS_TOKEN_EXPIRY_TIME);
            response.addCookie(accessTokenCookie);
            log.info("Access token set in secure HTTP-only cookie");

            userConfigService.saveUserConfig(tokenResponse, email);
            log.info("User config saved for user: {}", email);

            MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User registered successfully", null);
            return ResponseEntity.ok(finalResponse);
        }

        log.error("Invalid refresh token or user not found for email: {}", email);
        throw CommonExceptions.invalidRequest("Invalid refresh token!");
    }


    private Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);           // Prevents XSS attacks
        cookie.setPath("/");                // Available for entire application
        cookie.setMaxAge(maxAge);           // Set expiration time
        boolean isProd = utilService.isProd();
        cookie.setSecure(isProd);             // Only send over HTTPS (set to false for local development)
        cookie.setAttribute("SameSite", isProd ? "None" : "Lax");
        cookie.setDomain(isProd ? "mohitsaini.in" : "localhost");
        return cookie;
    }
}
