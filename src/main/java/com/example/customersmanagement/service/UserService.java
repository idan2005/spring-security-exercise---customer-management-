package com.example.customersmanagement.service;

import com.example.customersmanagement.dto.CreateUserRequest;
import com.example.customersmanagement.dto.UpdateUserRequest;
import com.example.customersmanagement.dto.UserProfile;
import com.example.customersmanagement.entity.Role;
import com.example.customersmanagement.entity.User;
import com.example.customersmanagement.repository.RoleRepository;
import com.example.customersmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    // Existing methods...
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public UserProfile getUserProfile(String username) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            return new UserProfile(userOptional.get());
        }
        throw new RuntimeException("User not found: " + username);
    }

    // New methods for admin functionality
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Assign default USER role
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        user.getRoles().add(userRole);
        return userRepository.save(user);
    }

    public User updateUser(String username, User updatedUser) {
        User existingUser = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields (don't update password here)
        if (updatedUser.getRoles() != null) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public void assignRoleToUser(String username, Integer roleId) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(String username, Integer roleId) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    // Statistics methods
    public long getTotalUsers() {
        return userRepository.count();
    }

    public List<User> getRecentUsers(int limit) {
        // This is a simplified version - you might want to add created_date field
        return userRepository.findAll().stream().limit(limit).toList();
    }

    public Map<String, Long> getUserCountByRole() {
        // Simplified implementation
        Map<String, Long> roleStats = new HashMap<>();
        roleStats.put("USER", userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getRoleName().equals("USER")))
                .count());
        roleStats.put("ADMIN", userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getRoleName().equals("ADMIN")))
                .count());
        return roleStats;
    }

    public long getActiveUsersCount() {
        return getTotalUsers(); // Simplified - you can add logic for "active" users
    }

    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", getTotalUsers());
        stats.put("roleDistribution", getUserCountByRole());
        return stats;
    }

    // User-specific methods
    public UserProfile updateUserProfile(String username, Map<String, String> updates) {
        // Implement profile updates
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update allowed fields only
        userRepository.save(user);
        return new UserProfile(user);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Map<String, Object> getUserAccountInfo(String username) {
        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("username", username);
        // Add more account info as needed
        return accountInfo;
    }

    public void updateUserSettings(String username, Map<String, Object> settings) {
        // Implement settings updates
    }

    public Map<String, Object> getUserActivity(String username) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("username", username);
        activity.put("message", "User activity tracking not implemented yet");
        return activity;
    }

    @Transactional(readOnly = true)
    public List<UserProfile> listUsersAsProfiles() {
        // loads roles in one go
        var users = userRepository.findAllWithRoles();
        return users.stream().map(UserProfile::new).toList();
    }
    @Transactional
    public UserProfile createUser(CreateUserRequest req) {
        Objects.requireNonNull(req, "Request is null");
        if (userRepository.existsById(req.username())) {
            throw new IllegalArgumentException("Username already exists: " + req.username());
        }

        var roles = (req.roles() == null ? List.<String>of() : req.roles())
                .stream()
                .map(roleService::getOrCreate)
                .collect(Collectors.toSet());

        var u = new User();
        u.setUsername(req.username());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRoles(roles);

        return new UserProfile(userRepository.save(u));
    }
    @Transactional(readOnly = true)
    public UserProfile getProfile(String username) {
        var u = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        return new UserProfile(u);
    }
    @Transactional
    public UserProfile updateUser(String username, UpdateUserRequest req) {
        var u = userRepository.findById(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        if (req != null) {
            if (req.password() != null && !req.password().isBlank()) {
                u.setPassword(passwordEncoder.encode(req.password()));
            }
            if (req.roles() != null) {
                var roles = req.roles().stream()
                        .map(roleService::getOrCreate)
                        .collect(Collectors.toSet());
                u.setRoles(roles);
            }
        }
        return new UserProfile(userRepository.save(u));
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        if (userRepository.existsById(username)) {
            userRepository.deleteById(username);
        }
    }
}