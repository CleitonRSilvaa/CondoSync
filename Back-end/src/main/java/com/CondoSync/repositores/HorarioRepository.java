package com.CondoSync.repositores;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CondoSync.models.Horario;
import com.CondoSync.models.StatusReserva;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {

        List<Horario> findAllByAreaIdOrderByHoraInicio(UUID id);

        Optional<Horario> findByAreaId(UUID id);

        @Query("SELECT h FROM Horario h WHERE h.area.id = :areaId AND NOT EXISTS " +
                        "(SELECT r FROM Reserva r WHERE r.data = :data AND r.horaInicio = h.horaInicio AND r.horaFim = h.horaFim)")
        List<Horario> findAvailableHorariosByAreaIdAndDate(@Param("areaId") UUID areaId, @Param("data") Date LocalDate);

        @Query("SELECT h FROM Horario h WHERE h.area.id = :areaId AND NOT EXISTS " +
                        "(SELECT r FROM Reserva r WHERE r.data = :data AND r.horaInicio = h.horaInicio AND r.horaFim = h.horaFim and r.statusReserva != :statusReserva)")
        List<Horario> findAvailableOrCanceledHorariosByAreaIdAndDate(@Param("areaId") UUID areaId,
                        @Param("data") LocalDate data,
                        @Param("statusReserva") StatusReserva statusReserva);

        // @Query("SELECT h FROM Horario h WHERE h.area.id = :areaId AND (NOT EXISTS ("
        // +
        // "SELECT r FROM Reserva r WHERE r.data = :data AND r.horaInicio = h.horaInicio
        // AND r.horaFim = h.horaFim AND r.statusReserva != 'CANCELADA'))")
        // List<Horario> findAvailableOrCanceledHorariosByAreaIdAndDate(@Param("areaId")
        // UUID areaId,
        // @Param("data") Date data);

}
