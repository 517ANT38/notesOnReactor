package com.app.testingService.configuration.security.support;

import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import com.app.testingService.configuration.security.auth.CurrentUserAuthenticationBearer;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>>{
    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));
    private final JwtVerifyHandler jwtVerifier;

    public ServerHttpBearerAuthenticationConverter(JwtVerifyHandler jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(ServerHttpBearerAuthenticationConverter::extract)
                .filter(matchBearerLength)
                .flatMap(isolateBearerValue)
                .flatMap(jwtVerifier::check)
                .flatMap(CurrentUserAuthenticationBearer::create);
    }

    public static Mono<String> extract(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION));
    }
}
