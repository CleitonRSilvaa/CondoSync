package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CondoSync.models.Reserva;
import com.CondoSync.models.StatusReserva;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByDataAndHoraInicioAndHoraFim(LocalDate data, LocalTime horaInicio, LocalTime horaFim);

    @Query("SELECT r FROM Reserva r WHERE r.data = :data AND r.horaInicio = :horaInicio AND r.horaFim = :horaFim AND r.statusReserva != :statusReserva")
    Optional<Reserva> findByDataAndHoraInicioAndHoraFimAndNotStatus(
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("statusReserva") StatusReserva statusReserva);

    List<Reserva> findAllByStatusReserva(StatusReserva statusReserva);
}
