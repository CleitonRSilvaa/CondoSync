package com.CondoSync.models.DTOs;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservaMoradorDTO {

    @NotNull(message = "O id do morador é obrigatorio")
    @Schema(type = "string", format = "uuid", example = "123e4567-e89b-12d3-a456-426614174000", description = "Id do morador", required = true)
    private UUID moradorId;

    @NotNull(message = "O id da area é obrigatorio")
    @Schema(type = "integer", example = "1", description = "Id da area", required = true)
    private UUID areaId;

    @NotNull(message = "O id do horario é obrigatorio")
    @Schema(type = "integer", example = "1", description = "Id do horario", required = true)
    private Integer horarioId;

    @NotNull(message = "A data é obrigatorio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", pattern = "yyyy-MM-dd", example = "2021-12-31", description = "Data da reserva no formato yyyy-MM-dd", required = true)
    private Date data;

}
