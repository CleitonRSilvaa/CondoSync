package com.CondoSync.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Reserva;
import com.CondoSync.models.ReservaMorador;
import com.CondoSync.models.StatusReserva;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.NewReservaDTO;
import com.CondoSync.models.DTOs.ReservaDTO;
import com.CondoSync.repositores.ReservaRepository;
import com.CondoSync.repositores.ReservaMoradorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class ReservaMoradorService {

    @Autowired
    private ReservaMoradorRepository reservaMoradorRepository;

    @Autowired
    private ReservaRepository reservaAreaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private UserService userService;

    @Autowired
    private MoradorService moradorService;

    public ReservaMorador save(@Valid ReservaMorador reservaMorador) {

        return reservaMoradorRepository.save(reservaMorador);
    }

    public List<ReservaDTO> listAll() {

        var reservasMoradores = reservaMoradorRepository.findAll();

        List<ReservaDTO> reservas = new ArrayList<>();

        for (ReservaMorador reservaMorador : reservasMoradores) {

            var r = new ReservaDTO();
            r.setId(reservaMorador.getId());
            r.setArea(reservaMorador.getArea().getName());
            r.setMorador(reservaMorador.getMorador().getNome());
            r.setData(reservaMorador.getReserva().getData());
            r.setHorario(
                    reservaMorador.getReserva().getHoraInicio() + " - " + reservaMorador.getReserva().getHoraFim());
            r.setStatus(reservaMorador.getReserva().getStatusReserva().getStatus());

            reservas.add(r);
        }

        reservas.sort((r1, r2) -> {
            // Primeiro, compara o status
            if (r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                    && !r2.getStatus().equals(StatusReserva.PENDENTE.getStatus())) {
                return -1;
            } else if (!r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                    && r2.getStatus().equals(StatusReserva.PENDENTE.getStatus())) {
                return 1;
            } else {
                return r1.getData().compareTo(r2.getData());
            }
        });

        return reservas;
    }

    public List<ReservaDTO> listAll(User user) {

        var morador = moradorService.findMoradorByEmail(user.getUsername());

        var reservasMoradores = reservaMoradorRepository.findByMorador_Id(morador.getId());

        List<ReservaDTO> reservas = new ArrayList<>();

        for (ReservaMorador reservaMorador : reservasMoradores) {

            var r = new ReservaDTO();
            r.setId(reservaMorador.getId());
            r.setArea(reservaMorador.getArea().getName());
            r.setMorador(reservaMorador.getMorador().getNome());
            r.setData(reservaMorador.getReserva().getData());
            r.setHorario(
                    reservaMorador.getReserva().getHoraInicio() + " - " + reservaMorador.getReserva().getHoraFim());
            r.setStatus(reservaMorador.getReserva().getStatusReserva().getStatus());

            reservas.add(r);
        }

        reservas.sort((r1, r2) -> {
            // Primeiro, compara o status
            if (r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                    && !r2.getStatus().equals(StatusReserva.PENDENTE.getStatus())) {
                return -1;
            } else if (!r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                    && r2.getStatus().equals(StatusReserva.PENDENTE.getStatus())) {
                return 1;
            } else {
                return r1.getData().compareTo(r2.getData());
            }
        });

        return reservas;

    }

    public ResponseEntity<?> cancelarReserva(Integer id) {

        var reservaMorador = reservaMoradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada!"));

        if (reservaMorador.getReserva().getStatusReserva().equals(StatusReserva.CANCELADA)) {
            throw new IllegalArgumentException("Reserva já cancelada!");
        }

        if (reservaMorador.getReserva().getData().before(Date.from(new Date().toInstant()))) {
            throw new IllegalArgumentException("Não é possível cancelar uma reserva passada!");
        }

        reservaMorador.getReserva().setStatusReserva(StatusReserva.CANCELADA);

        reservaMoradorRepository.save(reservaMorador);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> reservarArea(NewReservaDTO reservaMoradorDTO, String email) {

        var horario = horarioService.findById(reservaMoradorDTO.getHorarioId());

        if (reservaAreaRepository.findByDataAndHoraInicioAndHoraFim(reservaMoradorDTO.getData(),
                horario.getHoraInicio(), horario.getHoraFim()).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe uma reserva cadastrada para a área com o mesmo horário!");
        }

        var morador = moradorService.findMoradorByEmail(email);

        var area = areaService.findById(reservaMoradorDTO.getAreaId());

        Reserva reserva = new Reserva();
        ReservaMorador reservaMorador = new ReservaMorador();

        reserva.setData(reservaMoradorDTO.getData());
        reserva.setHoraInicio(horario.getHoraInicio());
        reserva.setHoraFim(horario.getHoraFim());
        reserva.setStatusReserva(StatusReserva.PENDENTE);

        reservaMorador.setMorador(morador);
        reservaMorador.setReserva(reserva);
        reservaMorador.setArea(area);

        reservaMoradorRepository.save(reservaMorador);

        return ResponseEntity.ok().build();

    }

}
