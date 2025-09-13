package com.example.customersmanagement.service;

import com.example.customersmanagement.entity.User;
import com.example.customersmanagement.model.CustomUserDetails;
import com.example.customersmanagement.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthApiUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthApiUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOptional.get();

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());

        // Defensive copy of roles to avoid ConcurrentModificationException
        Collection<GrantedAuthority> authorities = new HashSet<>(user.getRoles())
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toSet());

        userDetails.setAuthorities(authorities);

        return userDetails;
    }
}
