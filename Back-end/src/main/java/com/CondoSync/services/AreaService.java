package com.CondoSync.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Image;
import com.CondoSync.models.Area;
import com.CondoSync.models.DTOs.AreaDTO;
import com.CondoSync.repositores.AreaRepository;
import com.CondoSync.repositores.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private MoradorService moradorService;

    @Autowired
    private ReservaRepository reservaAreaRepository;

    // @Autowired
    // private ReservaMoradorService reservaMoradorService;

    public Area register(@Valid AreaDTO areaDTO) {

        var area = new Area();
        List<Image> images = new ArrayList<>();

        area.setName(areaDTO.getName());
        area.setDescription(areaDTO.getDescription());
        area.setPrice(areaDTO.getPrice());
        area.setStatus(true);
        for (var img : areaDTO.getImages()) {
            Image image = new Image(img.path(), img.name());
            image.setArea(area);
            images.add(image);
        }
        area.setImages(images);

        return areaRepository.save(area);

    }

    public List<AreaDTO> listAll() {
        List<AreaDTO> areas = areaRepository.findAllByOrderByNameAsc().stream()
                .map(area -> {
                    AreaDTO areaDTO = new AreaDTO();
                    areaDTO.setId(area.getId());
                    areaDTO.setName(area.getName());
                    areaDTO.setDescription(area.getDescription());
                    areaDTO.setPrice(area.getPrice());

                    List<AreaDTO.Image> images = area.getImages().stream()
                            .map(img -> new AreaDTO.Image(img.getPath(), img.getName()))
                            .collect(Collectors.toList());
                    areaDTO.setImages(images);

                    return areaDTO;
                })
                .collect(Collectors.toList());

        return areas;
    }

    public Area findById(UUID id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Area with id " + id + " not found"));
    }

    // public ResponseEntity<?> reservarArea(ReservaMoradorDTO reservaMoradorDTO) {

    // var area = findById(reservaMoradorDTO.getAreaId());

    // var horario = horarioService.findById(reservaMoradorDTO.getHorarioId());

    // if
    // (reservaAreaRepository.findByDataAndHoraInicioAndHoraFim(reservaMoradorDTO.getData(),
    // horario.getHoraInicio(), horario.getHoraFim()).isPresent()) {
    // throw new IllegalArgumentException(
    // "Já existe uma reserva cadastrada para a área com o mesmo horário!");
    // }

    // // var morador = moradorService.findById(reservaMoradorDTO.getMoradorId());

    // var morador = moradorService.findMoradorByEmail("nome@example.com");

    // // if (area.getReservas().stream().anyMatch(r ->
    // // r.getHoraInicio().equals(horario.getHoraInicio())
    // // || r.getHoraFim().equals(horario.getHoraFim()))) {
    // // throw new IllegalArgumentException("Já existe uma reserva cadastrada para
    // a
    // // área com id: "
    // // + area.getId() + " com o mesmo horário de início ou fim");
    // // }

    // Reserva reserva = new Reserva();
    // ReservaMorador reservaMorador = new ReservaMorador();

    // reserva.setData(reservaMoradorDTO.getData());
    // reserva.setHoraInicio(horario.getHoraInicio());
    // reserva.setHoraFim(horario.getHoraFim());
    // reserva.setStatusReserva(StatusReserva.PENDENTE);

    // reservaMorador.setMorador(morador);
    // reservaMorador.setReserva(reserva);

    // reservaMoradorService.save(reservaMorador);

    // // areaRepository.save(area);

    // return ResponseEntity.ok().build();

    // }

}
