package com.example.authority_example;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthorityEntity {
    private AuthorityCode code;
    private String description;

    public AuthorityEntity(AuthorityCode code, String description) {
        this.code = code;
        this.description = description;
    }
}
