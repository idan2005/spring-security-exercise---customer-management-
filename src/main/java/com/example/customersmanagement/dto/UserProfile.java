package com.example.customersmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String username;
    private Set<String> roles;
    private String displayName;

    // Constructor from User entity
    public UserProfile(com.example.customersmanagement.entity.User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(java.util.stream.Collectors.toSet());
        this.displayName = user.getUsername(); // or create a displayName field in User
    }
}