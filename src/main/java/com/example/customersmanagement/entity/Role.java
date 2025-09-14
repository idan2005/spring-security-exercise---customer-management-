package com.example.customersmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ❗ never include collections
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String roleName;

    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY) // ❗ LAZY
    @JsonIgnore                       // avoid recursion when returning Role as JSON
    @ToString.Exclude                 // don’t print collections
    @EqualsAndHashCode.Exclude        // don’t use in equals/hashCode
    private Set<User> users = new HashSet<>();
}
