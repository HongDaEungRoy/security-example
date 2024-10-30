package com.example.authority_example.service;

import com.example.authority_example.domain.EntityRepository;
import com.example.authority_example.domain.UserEntity;
import com.example.authority_example.global.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EntityRepository entityRepository;

    public String login(String username, String password) {
        UserEntity userEntity = entityRepository.findByName(username);
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            return jwtUtil.generateToken(userEntity.getAuthorities(), userEntity.getDepartment().getName(), userEntity.getName());
        }
        throw new BadCredentialsException("Invalid username or password");
    }
}
