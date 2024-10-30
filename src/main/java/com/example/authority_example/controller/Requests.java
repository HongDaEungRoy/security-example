package com.example.authority_example.controller;

import lombok.Getter;

public class Requests {

    @Getter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Getter
    public static class UpdateUserRequest {
        private String newName;
    }

    @Getter
    public static class CreateUserRequest {
        private String username;
        private String password;
        private String department;
        private String role;
    }

    @Getter
    public static class DeleteUserRequest {
        private String username;
        private String department;
    }
}
