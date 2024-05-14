package com.CondoSync.controllers;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.Area;
import com.CondoSync.models.DTOs.AreaDTO;
import com.CondoSync.services.AreaService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/area")
@Validated
public class AreaController {
    @Autowired
    private AreaService areaService;

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

}
