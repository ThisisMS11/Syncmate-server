package com.SyncMate.SyncMate.filter;

import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.repository.APIKeyRepository;
import com.SyncMate.SyncMate.services.APIKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class APIKeyAuthFilter extends OncePerRequestFilter {

    private final APIKeyRepository apiKeyRepository;
    private final APIKeyService apiKeyService;

    @Autowired
    APIKeyAuthFilter(APIKeyRepository apiKeyRepository, APIKeyService apiKeyService) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKeyHeader = request.getHeader("x-api-key");

        try {
            if (apiKeyHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (apiKeyService.validateAPIKey(apiKeyHeader)) {
                    User user = apiKeyRepository.findUserWithApiKey(apiKeyHeader).getUser();

                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.name()))
                            .toList();

                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            "",
                            authorities
                    );

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("API Key authentication failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
