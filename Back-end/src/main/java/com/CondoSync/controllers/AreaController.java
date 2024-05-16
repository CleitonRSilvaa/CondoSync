package com.CondoSync.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.DTOs.AreaDTO;
import com.CondoSync.models.DTOs.HorarioDTO;
import com.CondoSync.models.DTOs.ReservaMoradorDTO;
import com.CondoSync.services.AreaService;
import com.CondoSync.services.HorarioService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/area")
@Validated
public class AreaController {
    @Autowired
    private AreaService areaService;

    @Autowired
    private HorarioService horarioService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AreaDTO areaDto) {

        var area = areaService.register(areaDto);
        if (area == null) {
            return new ResponseEntity<>(400, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(area, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAll() {
        return new ResponseEntity<>(areaService.listAll(), HttpStatus.OK);
    }

    @PostMapping("schedule/register")
    public ResponseEntity<?> registerHorario(@RequestBody @Valid HorarioDTO horarioDto) {
        return new ResponseEntity<>(horarioService.register(horarioDto), HttpStatus.CREATED);
    }

    @GetMapping("schedule/list")
    public ResponseEntity<?> listAllHorarios() {
        return new ResponseEntity<>(horarioService.listAll(), HttpStatus.OK);
    }

    @GetMapping("schedule/find")
    public ResponseEntity<?> findHorario(@RequestParam Integer id) {
        return new ResponseEntity<>(horarioService.findById(id), HttpStatus.OK);
    }

    @GetMapping("{areaId}/schedule")
    public ResponseEntity<?> findHorarioByArea(@PathVariable UUID areaId) {
        return new ResponseEntity<>(horarioService.findByAreaId(areaId), HttpStatus.OK);
    }

    @GetMapping("{areaId}/schedules")
    public ResponseEntity<?> findAllHorariosByArea(@PathVariable UUID areaId) {
        return new ResponseEntity<>(horarioService.findAllByAreaId(areaId), HttpStatus.OK);
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> reserve(@RequestBody @Valid ReservaMoradorDTO reservaMoradorDTO) {

        return new ResponseEntity<>(areaService.reservarArea(reservaMoradorDTO), HttpStatus.CREATED);
    }

}
