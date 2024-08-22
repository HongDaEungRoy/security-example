package com.example.authority_example;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }

}
