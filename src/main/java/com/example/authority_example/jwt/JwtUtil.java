package com.example.authority_example.jwt;


import com.example.authority_example.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(CustomUserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        claims.put("department", user.getDepartment().getName());
        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails user) {
        String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        List<String> authorities = extractClaim(token, claims -> claims.get("authorities", List.class));
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String extractDepartment(String token) {
        return extractClaim(token, claims -> claims.get("department", String.class));
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaim(token));
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

}
