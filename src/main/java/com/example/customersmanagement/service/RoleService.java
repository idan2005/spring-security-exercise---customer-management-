package com.example.customersmanagement.service;

import com.example.customersmanagement.entity.Role;
import com.example.customersmanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Integer roleId, Role role) {
        role.setId(roleId);
        return roleRepository.save(role);
    }

    public void deleteRole(Integer roleId) {
        roleRepository.deleteById(roleId);
    }

    public long getTotalRoles() {
        return roleRepository.count();
    }
    @Transactional
    public Role getOrCreate(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName(roleName);
                    return roleRepository.save(r);
                });
    }
}