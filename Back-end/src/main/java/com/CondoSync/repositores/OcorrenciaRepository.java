package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Ocorrencia;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Integer> {

}
