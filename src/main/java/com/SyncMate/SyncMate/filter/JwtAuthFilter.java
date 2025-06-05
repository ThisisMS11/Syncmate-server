package com.SyncMate.SyncMate.filter;

import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String username = null;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 🔄 Get token from cookie instead of Authorization header
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                } else {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        Optional<Cookie> authCookie = Arrays.stream(cookies)
                                .filter(c -> "access_token".equals(c.getName()))
                                .findFirst();
                        if (authCookie.isPresent()) {
                            token = authCookie.get().getValue();
                        }
                    }
                }

                if (token != null) {
                    username = jwtService.extractUsername(token);
                }

                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.validateAccessToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            } catch (Exception e) {
                log.error("Authentication failed: {}", e.getMessage());
                throw CommonExceptions.unauthorizedAccess();
            }
        }

        filterChain.doFilter(request, response);
    }

}
