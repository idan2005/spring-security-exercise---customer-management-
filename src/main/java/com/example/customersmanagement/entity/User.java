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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"username", "role_id"}),
            indexes = {
                    @Index(name = "idx_user_roles_username", columnList = "username"),
                    @Index(name = "idx_user_roles_role_id", columnList = "role_id")
            }
    )
    private Set<Role> roles = new HashSet<>();
}
