package com.SyncMate.SyncMate;

import com.SyncMate.SyncMate.controller.PublicController;
import com.SyncMate.SyncMate.dto.LoginRequest;
import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.Role;
import com.SyncMate.SyncMate.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PublicTests {

    @Mock
    private UserService userService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserConfigService userConfigService;
    @Mock
    private UtilService utilService;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private PublicController publicController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        ResponseEntity<MakeResponseDto<?>> response = publicController.registerUser(request);
        verify(userService, times(1)).saveNewUser(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }

    @Test
    void testAuthenticateAndGetTokenSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtService.generateAccessToken("test@example.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("test@example.com")).thenReturn("refresh-token");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRoles(Set.of(Role.USER));

        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);

        ResponseEntity<MakeResponseDto<?>> response = publicController.authenticateAndGetToken(loginRequest, httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User logged in successfully", response.getBody().getMessage());
    }

    @Test
    void testRefreshTokenFromBody() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("refresh_token", "valid-refresh-token");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(null);
        when(jwtService.validateRefreshToken(eq("valid-refresh-token"), any())).thenReturn(true);
        when(jwtService.generateAccessToken("test@example.com")).thenReturn("new-access-token");

        ResponseEntity<MakeResponseDto<?>> response = publicController.refreshToken(requestMap, httpServletRequest, httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }

    @Test
    void testRefreshTokenFromCookies() {
        Cookie cookie = new Cookie("refresh_token", "cookie-refresh-token");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});

        when(jwtService.extractUsername("cookie-refresh-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(null);
        when(jwtService.validateRefreshToken(eq("cookie-refresh-token"), any())).thenReturn(true);
        when(jwtService.generateAccessToken("test@example.com")).thenReturn("cookie-new-access-token");

        ResponseEntity<MakeResponseDto<?>> response = publicController.refreshToken(null, httpServletRequest, httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
    }


}
