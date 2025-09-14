package com.example.customersmanagement.dto;


import java.util.List;

public record UpdateUserRequest(
        String password,           // null/"" = לא לשנות סיסמה
        List<String> roles
) {}
