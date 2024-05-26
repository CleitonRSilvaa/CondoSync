package com.CondoSync.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    public List<ReservaDTO> getAllReservas() {
        return reservaMoradorRepository.findAll().stream()
                .map(reservaMorador -> {
                    ReservaDTO r = new ReservaDTO();
                    r.setId(reservaMorador.getId());
                    r.setArea(reservaMorador.getArea().getName());
                    r.setMorador(reservaMorador.getMorador().getNome());
                    r.setData(reservaMorador.getReserva().getData());
                    r.setHorario(
                            reservaMorador.getReserva().getHoraInicio() + " - "
                                    + reservaMorador.getReserva().getHoraFim());
                    r.setStatus(reservaMorador.getReserva().getStatusReserva().getStatus());

                    return r;
                })
                .sorted((r1, r2) -> {
                    // Prioridade ao status "Pendente"
                    int statusComparison = r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                            ? (r2.getStatus().equals(StatusReserva.PENDENTE.getStatus()) ? 0 : -1)
                            : (r2.getStatus().equals(StatusReserva.PENDENTE.getStatus()) ? 1 : 0);

                    if (statusComparison != 0) {
                        return statusComparison;
                    }
                    // Se o status for o mesmo, ordenar por data de forma decrescente
                    return r2.getData().compareTo(r1.getData());
                })
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> listAll(User user) {

        var morador = moradorService.findMoradorByEmail(user.getUsername());

        return reservaMoradorRepository.findByMorador_Id(morador.getId()).stream()
                .map(reservaMorador -> {
                    ReservaDTO r = new ReservaDTO();
                    r.setId(reservaMorador.getId());
                    r.setArea(reservaMorador.getArea().getName());
                    r.setMorador(reservaMorador.getMorador().getNome());
                    r.setData(reservaMorador.getReserva().getData());
                    r.setHorario(
                            reservaMorador.getReserva().getHoraInicio() + " - "
                                    + reservaMorador.getReserva().getHoraFim());
                    r.setStatus(reservaMorador.getReserva().getStatusReserva().getStatus());

                    return r;
                })
                .sorted((r1, r2) -> {

                    int statusComparison = r1.getStatus().equals(StatusReserva.PENDENTE.getStatus())
                            ? (r2.getStatus().equals(StatusReserva.PENDENTE.getStatus()) ? 0 : -1)
                            : (r2.getStatus().equals(StatusReserva.PENDENTE.getStatus()) ? 1 : 0);

                    if (statusComparison != 0) {
                        return statusComparison;
                    }
                    return r2.getData().compareTo(r1.getData());
                })
                .collect(Collectors.toList());

    }

    public ResponseEntity<?> cancelarReserva(Integer id) {

        var reservaMorador = reservaMoradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada!"));

        if (reservaMorador.getReserva().getStatusReserva().equals(StatusReserva.CANCELADA)) {
            throw new IllegalArgumentException("Reserva já cancelada!");
        }

        if (reservaMorador.getReserva().getData().isBefore(LocalDate.now())) {
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

    @Async
    // @Scheduled(fixedRate = 30000)
    public void atualizarStatusReservas() {
        LocalDateTime agora = LocalDateTime.now();
        List<Reserva> reservas = reservaAreaRepository.findAllByStatusReserva(StatusReserva.PENDENTE);

        reservas.forEach(reserva -> {

            LocalDateTime dataHoraFim = reserva.getData().atTime(reserva.getHoraFim());

            if (dataHoraFim.isBefore(agora) && reserva.getStatusReserva().equals(StatusReserva.PENDENTE)) {
                reserva.setStatusReserva(StatusReserva.CANCELADA);
                reservaAreaRepository.save(reserva);
            }
            if (dataHoraFim.isBefore(agora) && reserva.getStatusReserva().equals(StatusReserva.APROVADA)) {
                reserva.setStatusReserva(StatusReserva.FINALIZADA);
                reservaAreaRepository.save(reserva);
            }

        });

    }

}
