package com.CondoSync.repositores;

import java.util.UUID;

import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.OcorrenciaMorador;

public interface OcorrenciaMoradorRepository extends JpaRepository<OcorrenciaMorador, Integer> {

  java.util.List<OcorrenciaMorador> findByMorador_Id(UUID moradorId);

}
