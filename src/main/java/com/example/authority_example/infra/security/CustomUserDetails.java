package com.example.authority_example.infra.security;

import com.example.authority_example.domain.DepartmentEntity;
import com.example.authority_example.domain.RoleEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private DepartmentEntity department;
    private List<RoleEntity> roles;
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    public CustomUserDetails(String username, String password, DepartmentEntity department, List<RoleEntity> roles) {
        this.username = username;
        this.password = password;
        this.department = department;
        this.roles = roles != null ? roles : Collections.emptyList();
        this.authorities = generateAuthorities();
    }

    private Set<SimpleGrantedAuthority> generateAuthorities() {
         Set<SimpleGrantedAuthority> authorities = this.roles.stream()
                 .map(role -> new SimpleGrantedAuthority(role.getCode().getValue()))
                 .collect(Collectors.toSet());
        this.roles.forEach(role ->
            role.getAuthorities().forEach(auth ->
                authorities.add(new SimpleGrantedAuthority(auth.getCode().name()))
            )
        );

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setAuthorities(HashSet<SimpleGrantedAuthority> simpleGrantedAuthorities) {
        this.authorities = simpleGrantedAuthorities;
    }
}
