package ru.belov.ourabroad.config.security;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String requiredUserId() {
        return requiredPrincipal().getUserId();
    }

    @Override
    public Optional<String> currentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(JwtUserPrincipal.class::isInstance)
                .map(p -> ((JwtUserPrincipal) p).getUserId());
    }

    @Override
    public JwtUserPrincipal requiredPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof JwtUserPrincipal jwt)) {
            throw new InsufficientAuthenticationException("JWT principal required");
        }
        return jwt;
    }
}
