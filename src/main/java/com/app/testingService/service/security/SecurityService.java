package com.app.testingService.service.security;





import com.app.testingService.models.Person;
import com.app.testingService.models.auth.TokenInfo;
import com.app.testingService.repos.PersonRepo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService implements Serializable {
    private final PersonRepo personRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String defaultExpirationTimeInSecondsConf;

    

    public TokenInfo generateAccessToken(Person person) {
        var claims = new HashMap<String, Object>() {{
            put("role", person.getRoles());
        }};

        return doGenerateToken(claims, person.getUsername(), person.getId().toString());
    }

    private TokenInfo doGenerateToken(Map<String, Object> claims, String issuer, String subject) {
        var expirationTimeInMilliseconds = Long.parseLong(defaultExpirationTimeInSecondsConf) * 1000;
        var expirationDate = new Date(new Date().getTime() + expirationTimeInMilliseconds);

        return doGenerateToken(expirationDate, claims, issuer, subject);
    }

    private TokenInfo doGenerateToken(Date expirationDate, Map<String, Object> claims, String issuer, String subject) {
        var createdDate = new Date();
        var token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenInfo.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .build();
    }

    public Mono<TokenInfo> authenticate(String username, String password) {
        return personRepository.findByUsername(username)
                .flatMap(person -> {
                    if (!person.isEnabled())
                        return Mono.error(new AuthException("Account disabled.", "person_ACCOUNT_DISABLED"));

                    if (!passwordEncoder.encode(password).equals(person.getPassword()))
                        return Mono.error(new AuthException("Invalid person password!", "INVALID_person_PASSWORD"));

                    return Mono.just(generateAccessToken(person).toBuilder()
                            .userId(person.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid person, " + username + " is not registered.", "INVALID_personNAME")));
    }
}
