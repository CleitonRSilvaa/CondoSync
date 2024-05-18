package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Area;
import com.CondoSync.models.Horario;
import com.CondoSync.models.DTOs.HorarioDTO;
import com.CondoSync.repositores.AreaRepository;
import com.CondoSync.repositores.HorarioRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private AreaRepository areaRepository;
    // @Autowired
    // private AreaService areaService;

    public HorarioDTO register(HorarioDTO horarioDTO) {

        var horario = new Horario();

        Area area = areaRepository.findById(horarioDTO.getAreaId()).orElseThrow(
                () -> new EntityNotFoundException("Área não encontrada com o id: " + horarioDTO.getAreaId()));

        horario.setArea(area);
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFim(horarioDTO.getHoraFim());

        if (horario.getHoraFim().isBefore(horario.getHoraInicio())
                || horario.getHoraFim().equals(horario.getHoraInicio())) {
            throw new IllegalArgumentException("Horario de fim não pode ser antes ou igual a Horario de inicio");
        }

        if (Duration.between(horario.getHoraInicio(), horario.getHoraFim()).toHours() < 1) {
            throw new IllegalArgumentException(
                    "A diferença entre o horário de início e fim deve ser de no mínimo uma hora.");
        }

        // if(horarioRepository.findByAreaId(horario.getArea().getId()).isPresent()) {
        // throw new IllegalArgumentException("Já existe um horário cadastrado para a
        // área com id: " + horario.getArea().getId());
        // }

        if (horarioRepository.findAllByAreaIdOrderByHoraInicio(horario.getArea().getId()).stream()
                .anyMatch(h -> h.getHoraInicio().equals(horario.getHoraInicio())
                        || h.getHoraFim().equals(horario.getHoraFim()))) {
            throw new IllegalArgumentException("Já existe um horário cadastrado para a área com id: "
                    + horario.getArea().getId() + " com o mesmo horário de início ou fim");
        }

        horarioRepository.save(horario);

        System.out.println(horario);

        return horarioDTO;
    }

    public Horario findById(Integer id) {
        return horarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Horario não encontrado"));
    }

    public void delete(Integer id) {
        horarioRepository.delete(findById(id));
    }

    public HorarioDTO findByAreaId(UUID id) {
        return new HorarioDTO(horarioRepository.findByAreaId(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario não encontrado para a area!")));
        // return new HorarioDTO(horarioRepository.findByAreaId(id).orElseThrow(() ->
        // new EntityNotFoundException("Horario não encontrado")));

    }

    public List<HorarioDTO> findAllByAreaId(UUID id) {
        return horarioRepository.findAllByAreaIdOrderByHoraInicio(id).stream().map(HorarioDTO::new).sorted(
                (h1, h2) -> h1.getHoraInicio().compareTo(h2.getHoraInicio())).collect(Collectors.toList());
    }

    public List<HorarioDTO> findAllByAreaIdAndDate(UUID id, String date) {
        return horarioRepository.findAllByAreaIdOrderByHoraInicio(id).stream().map(HorarioDTO::new).sorted(
                (h1, h2) -> h1.getHoraInicio().compareTo(h2.getHoraInicio())).collect(Collectors.toList());

    }

    public List<HorarioDTO> getAvailableHorarios(UUID areaId, Date data) {
        return horarioRepository.findAvailableHorariosByAreaIdAndDate(areaId, data).stream().map(HorarioDTO::new)
                .sorted((h1, h2) -> h1.getHoraInicio().compareTo(h2.getHoraInicio())).collect(Collectors.toList());
    }

    public List<HorarioDTO> listAll() {
        return horarioRepository.findAll().stream().map(HorarioDTO::new).collect(Collectors.toList());
    }

}
