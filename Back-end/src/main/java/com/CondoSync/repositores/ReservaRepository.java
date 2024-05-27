package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Reserva;
import com.CondoSync.models.StatusReserva;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByDataAndHoraInicioAndHoraFim(LocalDate data, LocalTime horaInicio, LocalTime horaFim);

    List<Reserva> findAllByStatusReserva(StatusReserva statusReserva);
}
