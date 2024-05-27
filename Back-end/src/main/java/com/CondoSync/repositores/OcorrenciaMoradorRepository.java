package com.CondoSync.repositores;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.OcorrenciaMorador;
import com.CondoSync.models.DTOs.OcorenciaDTO;

public interface OcorrenciaMoradorRepository extends JpaRepository<OcorrenciaMorador, Integer> {

  List<OcorrenciaMorador> findByMorador_Id(UUID moradorId);

  OcorrenciaMorador findByOcorrencia_Id(Integer id);

}
