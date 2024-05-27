package com.CondoSync.services;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import com.CondoSync.models.Role;
import com.CondoSync.repositores.RoleRepository;

@Service
@Slf4j
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {

        return roleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Role with id " + id + " not found"));
    }

    public Role getRoleByNome(String nome) {

        var role = roleRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Role " + nome + " not found"));
        return role;
    }

    public Role createRole(Role role) {

        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {

        this.getRoleById(role.getId());

        return roleRepository.save(role);
    }

    public Role patchRole(Role role) {

        this.getRoleById(role.getId());

        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        this.getRoleById(role.getId());
        roleRepository.delete(role);
    }

    public boolean existsByNome(String nome) {
        log.info("Checking if role {} exists", nome);
        return roleRepository.existsByNome(nome);

    }

}
