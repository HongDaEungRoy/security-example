package com.example.authority_example.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class UserEntity {
    private String name;
    private String password;
    private DepartmentEntity department;
    private List<RoleEntity> roles;

    public UserEntity(String name, String password, DepartmentEntity department, List<RoleEntity> roles) {
        this.name = name;
        this.password = password;
        this.department = department;
        this.roles = roles;
    }

    public void updateName(String name){
        this.name = name;
    }

    public List<String> getAuthorities() {
        List<String> authorities = new ArrayList<>(this.roles.stream()
                .map(role -> role.getCode().getValue())
                .toList());
        this.roles.forEach(role ->
            role.getAuthorities().forEach(auth ->
                authorities.add(auth.getCode().name())
            )
        );

        return authorities;
    }

}
