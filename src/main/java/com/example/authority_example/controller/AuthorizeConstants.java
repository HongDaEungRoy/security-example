package com.example.authority_example.controller;


public class AuthorizeConstants {
    public static final String DELETE_USER_AUTHORITY = "hasAuthority('DELETE') and #deleteUserRequest.getDepartment() == authentication.principal.getDepartment().getName()";
    public static final String GET_USER_ALL_AUTHORITY = "hasAnyRole('ADMIN', 'SUPER_ADMIN', 'HYPER_SUPER_ADMIN')";
}
