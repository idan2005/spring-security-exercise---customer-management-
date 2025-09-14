package com.example.customersmanagement.repository;

import com.example.customersmanagement.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> { // Changed from Integer to String

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    boolean existsByUsername(String admin);

    @EntityGraph(value = "User.withRoles", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select u from User u")
    List<User> findAllWithRoles();

    @EntityGraph(value = "User.withRoles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findWithRolesByUsername(@Param("username") String username);

}