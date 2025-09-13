package com.example.customersmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Column(name = "username",nullable = false,unique = true)
    @Id
    private String username;
    @Column(name = "password",nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_roles",                           // join table name
            joinColumns = @JoinColumn(name = "username"),  // FK to User
            inverseJoinColumns = @JoinColumn(name = "role_id") // FK to Role
    )
    private Set<Role> roles = new HashSet<>();
}
