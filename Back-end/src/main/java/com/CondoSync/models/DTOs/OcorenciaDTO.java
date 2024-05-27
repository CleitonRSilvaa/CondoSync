package com.CondoSync.models.DTOs;

import java.util.Date;

import com.CondoSync.models.Ocorrencia;
import com.CondoSync.models.OcorrenciaMorador;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OcorenciaDTO {

  private Integer id;

  @NotEmpty(message = "O titulo é obrigatorio")
  @Size(min = 5, max = 100, message = "O titulo deve ter entre 5 e 100 caracteres")
  private String title;

  @NotEmpty(message = "A descrição é obrigatorio")
  @Size(min = 5, max = 1000, message = "A descrição deve ter entre 5 e 1000 caracteres")
  private String description;

  private String status;

  @Max(value = 1000, message = "A resolução deve ter no maximo 1000 caracteres")
  private String resolution;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date creation;

  private MoradorSimplesDTO morador;

  public OcorenciaDTO(Ocorrencia ocorrencia) {
    this.id = ocorrencia.getId();
    this.title = ocorrencia.getTitle();
    this.description = ocorrencia.getDescription();
    this.status = ocorrencia.getStatus().getStatus();
    this.resolution = ocorrencia.getResolution();
    this.creation = Date.from(ocorrencia.getCreation());
  }

  public OcorenciaDTO(OcorrenciaMorador OcorrenciaMorador) {
    Ocorrencia ocorrencia = OcorrenciaMorador.getOcorrencia();
    this.id = ocorrencia.getId();
    this.title = ocorrencia.getTitle();
    this.description = ocorrencia.getDescription();
    this.status = ocorrencia.getStatus().getStatus();
    this.resolution = ocorrencia.getResolution();
    this.creation = Date.from(ocorrencia.getCreation());
    this.morador = new MoradorSimplesDTO(OcorrenciaMorador.getMorador());
  }
}