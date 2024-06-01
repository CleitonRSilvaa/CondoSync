package com.CondoSync.repositores;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Morador;
import com.CondoSync.models.ReservaMorador;

public interface ReservaMoradorRepository extends JpaRepository<ReservaMorador, Integer> {

    List<ReservaMorador> findByMorador(Morador morador);

    List<ReservaMorador> findByMorador_Id(UUID moradorId);

    ReservaMorador findByReserva_Id(Integer reservaId);
}
