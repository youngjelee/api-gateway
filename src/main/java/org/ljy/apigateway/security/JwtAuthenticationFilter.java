package org.ljy.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String SECRET_KEY = "ASDFASDFASDFASDFASDFASDFASDFASDFASDFASDFASDFASDFASDFASDFAS";
    private static final SecretKey SIGN_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange);

        if (token == null) {
            return chain.filter(exchange);
        }

        return authenticate(token)
            .flatMap(auth -> chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl(auth)))))
            .switchIfEmpty(chain.filter(exchange));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private Mono<Authentication> authenticate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

            Authentication auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.emptyList());
            return Mono.just(auth);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
