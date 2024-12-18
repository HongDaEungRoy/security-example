package com.example.authority_example.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepartmentEntity {
    private String name;

    public DepartmentEntity(String name) {
        this.name = name;
    }
}
