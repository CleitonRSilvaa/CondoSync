package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.MoradorDTO;
import com.CondoSync.services.MoradorService;
import com.CondoSync.services.RoleService;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/morador")
@Validated
public class MoradorController {

    @Autowired
    private MoradorService moradorService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid MoradorDTO moradorDTO) {

        var morador = moradorService.register(moradorDTO);
        if (morador == null) {
            return new ResponseEntity<>(400, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(moradorDTO, HttpStatus.CREATED);
    }

}
