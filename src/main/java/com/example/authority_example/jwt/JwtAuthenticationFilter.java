package com.example.authority_example.jwt;


import com.example.authority_example.CustomUserDetails;
import com.example.authority_example.DepartmentEntity;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);
            String department = jwtUtil.extractDepartment(jwt);

            CustomUserDetails userDetails = new CustomUserDetails(username, "", new DepartmentEntity(department), null);
            userDetails.setAuthorities(new HashSet<>(authorities));

            if (jwtUtil.validateToken(jwt, userDetails)) {
             UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails, null, userDetails.getAuthorities()
             );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}
