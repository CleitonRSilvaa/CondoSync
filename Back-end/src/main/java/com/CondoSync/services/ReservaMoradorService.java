package com.CondoSync.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
import com.CondoSync.models.DTOs.UpdateStatusReservaDTO;
import com.CondoSync.repositores.ReservaRepository;
import com.CondoSync.repositores.ReservaMoradorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservaMoradorService {

    @Autowired
    @Lazy
    private ReservaMoradorRepository reservaMoradorRepository;

    @Autowired
    @Lazy

    private ReservaRepository reservaAreaRepository;

    @Autowired
    @Lazy

    private AreaService areaService;

    @Autowired
    @Lazy

    private HorarioService horarioService;

    @Autowired
    @Lazy

    private UserService userService;

    @Autowired
    @Lazy
    private MoradorService moradorService;

    @Autowired
    ApiPushManagerService apiPushManagerService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Value("${scheduled.task.fixedRate.minutes}")
    public long fixedRateMinutes;

    public long fixedRateMillis() {
        return this.fixedRateMinutes * 60 * 1000;
    }

    public ReservaMorador save(@Valid ReservaMorador reservaMorador) {

        return reservaMoradorRepository.save(reservaMorador);
    }

    public List<ReservaDTO> getAllReservas() {
        return reservaMoradorRepository.findAll().stream()
                .map(reservaMorador -> {
                    ReservaDTO r = new ReservaDTO();
                    r.setId(reservaMorador.getReserva().getId());
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
                    r.setId(reservaMorador.getReserva().getId());
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

        var reservaMorador = reservaAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada!"));

        if (reservaMorador.getStatusReserva().equals(StatusReserva.CANCELADA)) {
            throw new IllegalArgumentException("Reserva já cancelada!");
        }

        if (reservaMorador.getData().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível cancelar uma reserva passada!");
        }

        reservaMorador.setStatusReserva(StatusReserva.CANCELADA);

        reservaAreaRepository.save(reservaMorador);

        log.info("Reserva cancelada com sucesso!");

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> reservarArea(NewReservaDTO reservaMoradorDTO, String email) {

        var horario = horarioService.findById(reservaMoradorDTO.getHorarioId());

        if (reservaAreaRepository.findByDataAndHoraInicioAndHoraFimAndNotStatus(reservaMoradorDTO.getData(),
                horario.getHoraInicio(), horario.getHoraFim(), StatusReserva.CANCELADA).isPresent()) {
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

        var subs = userSubscriptionService.findSubscriptionsByUserStatusAndRole("ADMIN");

        var payload = new ApiPushManagerService.Payload();

        payload.setTitle("Nova Reserva registrada");
        payload.setBody("Nova Reserva: em  nome de " + morador.getNome() + " para a área " + area.getName() + " no dia "
                + reserva.getData() + " das " + reserva.getHoraInicio() + " às " + reserva.getHoraFim());
        payload.setIcon("https://condo-sync.vercel.app/imagens/logo2.png");
        payload.setUrl("https://condo-sync.vercel.app/admin/gerenciar-reservas.html");

        apiPushManagerService.sendNotification(subs, payload);

        return ResponseEntity.ok().build();

    }

    @Async
    @Scheduled(fixedRateString = "#{@fixedRateMillis}", initialDelay = 1000 * 60 * 1)
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

    public ResponseEntity<?> updateReserva(Integer id, UpdateStatusReservaDTO reservaMoradorDto) {

        var reservaMorador = reservaAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada!"));

        if (reservaMoradorDto.getStatus() == null) {
            throw new IllegalArgumentException("Status da reserva é obrigatório!");
        }

        if (reservaMorador.getData().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível alterar uma reserva passada!");
        }

        if (reservaMorador.getStatusReserva().equals(StatusReserva.CANCELADA)) {
            throw new IllegalArgumentException("Reserva já cancelada!");
        }

        reservaMorador.setStatusReserva(reservaMoradorDto.getStatus());

        reservaAreaRepository.save(reservaMorador);

        var reservaAreaMorador = reservaMoradorRepository.findByReserva_Id(reservaMorador.getId());

        UUID uuid = userService.findByUserName(reservaAreaMorador.getMorador().getEmail()).get().getId();

        var subs = userSubscriptionService.getSubscriptions(uuid);

        var payload = new ApiPushManagerService.Payload();
        payload.setTitle("Reserva Atualizada");
        payload.setBody("Sua reserva foi atualizada para: " + reservaMoradorDto.getStatus().getStatus());
        payload.setUrl("https://condo-sync.vercel.app/morador/espaco-agendamento/index.html");
        payload.setIcon("https://condo-sync.vercel.app/imagens/logo2.png");

        apiPushManagerService.sendNotification(subs, payload);

        log.info("Reserva atualizada com sucesso!");

        return ResponseEntity.ok().build();
    }

}
