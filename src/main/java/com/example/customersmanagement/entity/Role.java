package com.example.customersmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(name = "idx_roles_role_name", columnList = "role_name", unique = true)
        }
)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name",nullable = false,unique = true)
    private String roleName;

    @Column(nullable = true)
    private String description;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }
}