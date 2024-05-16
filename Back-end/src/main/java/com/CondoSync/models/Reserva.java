package com.CondoSync.models;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservas_areas", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "data_reserva", "hora_inicio", "hora_fim" })
})
@Getter
@Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Integer id;

    @Column(name = "data_reserva")
    private Date data;

    @NotNull(message = "O horario de inicio é obrigatorio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "O horario de fim é obrigatorio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private StatusReserva statusReserva;

    @CreationTimestamp
    private Instant creation;

    @UpdateTimestamp
    private Instant upudate;

}
