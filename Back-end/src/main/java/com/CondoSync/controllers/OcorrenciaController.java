package com.CondoSync.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.OcorenciaDTO;
import com.CondoSync.services.OcorrenciaMoradorService;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ocorrencia")
@Validated
public class OcorrenciaController {

    private final UserService userService;
    private final OcorrenciaMoradorService ocorrenciaMoradorService;

    public OcorrenciaController(UserService userService, OcorrenciaMoradorService ocorrenciaMoradorService) {
        this.userService = userService;
        this.ocorrenciaMoradorService = ocorrenciaMoradorService;
    }

    @PreAuthorize("hasAuthority('SCOPE_MORADOR')")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            JwtAuthenticationToken jwtAuthenticationToken, @RequestBody @Valid OcorenciaDTO ocorenciaDTO) {
        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        return new ResponseEntity<>(ocorrenciaMoradorService.createOcorrenciaMorador(ocorenciaDTO, user.getUsername()),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_MORADOR')")
    @GetMapping("/list")
    public ResponseEntity<?> list(JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        return new ResponseEntity<>(ocorrenciaMoradorService.getOcorrenciaMorador(user.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_MORADOR')")
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable Integer id) {
        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        return new ResponseEntity<>(ocorrenciaMoradorService.cancelOcorrenciaMorador(id, user.getUsername()), HttpStatus.NO_CONTENT);
    }

}
