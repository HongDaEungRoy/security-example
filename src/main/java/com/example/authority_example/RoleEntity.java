package com.example.authority_example;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RoleEntity {
    private RoleCode code;
    private List<AuthorityEntity> authorities;

    public RoleEntity(RoleCode code, List<AuthorityEntity> authorities) {
        this.code = code;
        this.authorities = authorities;
    }
}
