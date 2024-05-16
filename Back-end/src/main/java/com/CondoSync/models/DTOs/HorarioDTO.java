package com.CondoSync.models.DTOs;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.CondoSync.models.Horario;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HorarioDTO {

    private Integer id;

    @NotNull(message = "O horario de inicio é obrigatorio")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(type = "string", pattern = "HH:mm", example = "09:30", description = "Hora de início no formato HH:mm", required = true)
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "O horario de fim é obrigatorio")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(type = "string", pattern = "HH:mm", example = "11:30", description = "Hora de fim no formato HH:mm", required = true)
    private LocalTime horaFim;

    @NotNull(message = "O id da area é obrigatorio")
    private UUID areaId;

    public HorarioDTO(LocalTime horaInicio, LocalTime horaFim, UUID areaId) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.areaId = areaId;
    }

    public HorarioDTO(Horario horario) {
        this.id = horario.getId();
        this.horaInicio = horario.getHoraInicio();
        this.horaFim = horario.getHoraFim();
    }

}
