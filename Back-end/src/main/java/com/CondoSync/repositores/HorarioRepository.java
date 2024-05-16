package com.CondoSync.repositores;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    List<Horario> findAllByAreaId(UUID id);

    Optional<Horario> findByAreaId(UUID id);

}
