package com.app.testingService.configuration.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.app.testingService.configuration.security.auth.UnauthorizedException;
import com.app.testingService.configuration.security.auth.UserPrincipal;
import com.app.testingService.repos.PersonRepo;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    
    private final PersonRepo pRepo;

    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();

        
        return pRepo.findById(principal.getId())
                .filter(user -> user.isEnabled())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}