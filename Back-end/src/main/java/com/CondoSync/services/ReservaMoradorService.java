package com.CondoSync.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Reserva;
import com.CondoSync.models.ReservaMorador;
import com.CondoSync.models.StatusReserva;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.ReservaMoradorDTO;
import com.CondoSync.repositores.ReservaAreaRepository;
import com.CondoSync.repositores.ReservaMoradorRepository;

import jakarta.validation.Valid;

@Service
public class ReservaMoradorService {

    @Autowired
    private ReservaMoradorRepository reservaMoradorRepository;

    @Autowired
    private ReservaAreaRepository reservaAreaRepository;

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

    public List<Reserva> listAll() {

        var reservasMoradores = reservaMoradorRepository.findAll();

        return reservasMoradores.stream().map(ReservaMorador::getReserva).toList();

    }

    public List<Reserva> listAll(User user) {

        var morador = moradorService.findMoradorByEmail(user.getUsername());

        var reservasMoradores = reservaMoradorRepository.findByMorador_Id(morador.getId());
        return reservasMoradores.stream().map(ReservaMorador::getReserva).toList();

    }

    public ResponseEntity<?> reservarArea(ReservaMoradorDTO reservaMoradorDTO) {

        var horario = horarioService.findById(reservaMoradorDTO.getHorarioId());

        if (reservaAreaRepository.findByDataAndHoraInicioAndHoraFim(reservaMoradorDTO.getData(),
                horario.getHoraInicio(), horario.getHoraFim()).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe uma reserva cadastrada para a área com o mesmo horário!");
        }

        var morador = moradorService.findMoradorByEmail("nome@example.com");

        Reserva reserva = new Reserva();
        ReservaMorador reservaMorador = new ReservaMorador();

        reserva.setData(reservaMoradorDTO.getData());
        reserva.setHoraInicio(horario.getHoraInicio());
        reserva.setHoraFim(horario.getHoraFim());
        reserva.setStatusReserva(StatusReserva.PENDENTE);

        reservaMorador.setMorador(morador);
        reservaMorador.setReserva(reserva);

        reservaMoradorRepository.save(reservaMorador);

        return ResponseEntity.ok().build();

    }

}
