package com.CondoSync.repositores;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Morador;

public interface MoradorRepository extends JpaRepository<Morador, UUID> {

    Optional<Morador> findByCpf(String cpf);

    Optional<Morador> findByEmail(String email);

    // Optional<Morador> findByBlocoAndApartamento(String bloco, String
    // apartamento);

}
