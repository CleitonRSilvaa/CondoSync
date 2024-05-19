package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Reserva;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.time.LocalTime;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByDataAndHoraInicioAndHoraFim(Date data, LocalTime horaInicio, LocalTime horaFim);

}
