package com.CondoSync.models.DTOs;

import com.CondoSync.components.StatusReservaDeserializer;
import com.CondoSync.models.StatusReserva;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateStatusReservaDTO {

  @NotNull(message = "O id da reserva é obrigatorio")
  private Integer id;

  @NotBlank(message = "O status da reserva é obrigatorio")
  private String status;

}
