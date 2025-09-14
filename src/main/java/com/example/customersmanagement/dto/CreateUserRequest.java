package com.example.customersmanagement.dto;
import java.util.List;

public record CreateUserRequest(
        String username,
        String password,
        List<String> roles // לדוגמה: ["USER"], ["ADMIN"], ["MANAGER","USER"]
) {}
