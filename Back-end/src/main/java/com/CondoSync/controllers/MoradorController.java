package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.MoradorDTO;
import com.CondoSync.services.MoradorService;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/morador")
@Validated
public class MoradorController {

    @Autowired
    private MoradorService moradorService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid MoradorDTO moradorDTO) {

        var morador = moradorService.register(moradorDTO);
        if (morador == null) {
            return new ResponseEntity<>(400, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(moradorDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_MORADOR')")
    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(JwtAuthenticationToken jwtAuthenticationToken) {

        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        // if (!userName.equals(user.getUsername()) &&
        // !user.getAuthorities().toString().toUpperCase().contains("ADMIN")) {
        // return new ResponseEntity<>("Usuário não autorizado",
        // HttpStatus.UNAUTHORIZED);
        // }

        return new ResponseEntity<>(moradorService.findProfiler(user.getUsername()), HttpStatus.OK);

    }

}
