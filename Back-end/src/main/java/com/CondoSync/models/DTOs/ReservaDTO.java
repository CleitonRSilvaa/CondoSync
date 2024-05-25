package com.CondoSync.models.DTOs;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservaDTO {

  Integer id;

  String area;

  String morador;

  @JsonFormat(pattern = "dd/MM/yyyy")
  LocalDate data;

  String horario;

  String status;

}
