package com.example.authority_example;


import lombok.Getter;

@Getter
public enum RoleCode {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN"),
    HYPER_SUPER_ADMIN("ROLE_HYPER_SUPER_ADMIN");

    private final String value;

    RoleCode(String value) {
        this.value = value;
    }
}
