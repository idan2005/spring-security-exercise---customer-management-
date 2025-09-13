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
    @Table(name = "roles")
public class Role {
    @Column(name = "id",nullable = false,unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_name",nullable = false,unique = true)
    private String roleName;
    @Column(name = "description",nullable = true)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}