package com.CondoSync.repositores;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.OcorrenciaMorador;

public interface OcorrenciaMoradorRepository extends JpaRepository<OcorrenciaMorador, Integer> {

  List<OcorrenciaMorador> findByMorador_Id(UUID moradorId);

}
