package com.CondoSync.models.DTOs;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewReservaDTO {

    @Schema(type = "string", example = "joao", description = "Nome do usuario", required = true)
    @NotNull(message = "O nome do usuario é obrigatorio")
    private String userName;

    @NotNull(message = "O id da area é obrigatorio")
    @Schema(type = "integer", example = "1", description = "Id da area", required = true)
    private UUID areaId;

    @NotNull(message = "O id do horario é obrigatorio")
    @Schema(type = "integer", example = "1", description = "Id do horario", required = true)
    private Integer horarioId;

    @NotNull(message = "A data é obrigatorio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", pattern = "yyyy-MM-dd", example = "2021-12-31", description = "Data da reserva no formato yyyy-MM-dd", required = true)
    private LocalDate data;

}
