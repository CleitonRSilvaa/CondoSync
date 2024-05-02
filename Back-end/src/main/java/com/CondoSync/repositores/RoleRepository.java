package com.CondoSync.repositores;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByNome(String nome);

    Optional<Role> findByNome(String nome);

}
