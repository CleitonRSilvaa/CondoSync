package com.CondoSync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.DTOs.ReservaMoradorDTO;
import com.CondoSync.services.AreaService;
import com.CondoSync.services.HorarioService;
import com.CondoSync.services.ReservaMoradorService;
import com.CondoSync.services.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/v1/reserva")
@Validated
public class ReservaController {

    @Autowired
    private ReservaMoradorService reservaMoradorService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid ReservaMoradorDTO reservaMoradorDto) {
        return new ResponseEntity<>(areaService.reservarArea(reservaMoradorDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> listAll(JwtAuthenticationToken jwtAuthenticationToken) {
        if (jwtAuthenticationToken == null) {
            return new ResponseEntity<>("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }

        System.out.println(jwtAuthenticationToken.getTokenAttributes());

        var user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject());

        // System.out.println(jwtAuthenticationToken.getTokenAttributes());
        // System.out.println(jwtAuthenticationToken.getToken().getSubject());
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication);
        // if (!(authentication instanceof AnonymousAuthenticationToken)) {
        // String currentUserName = authentication.getName();
        // return new ResponseEntity<>(currentUserName, HttpStatus.OK);
        // } else {
        // return new ResponseEntity<>("Usuário não autenticado",
        // HttpStatus.UNAUTHORIZED);
        // }

        return new ResponseEntity<>("", HttpStatus.OK);

    }

}
