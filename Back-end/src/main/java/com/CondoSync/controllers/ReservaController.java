package com.CondoSync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;

import com.CondoSync.models.DTOs.NewReservaDTO;
import com.CondoSync.models.DTOs.UpdateStatusReservaDTO;
import com.CondoSync.services.ReservaMoradorService;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/v1/reserva")
@Validated
public class ReservaController {

    @Autowired
    @Lazy
    private ReservaMoradorService reservaMoradorService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MORADOR')")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            JwtAuthenticationToken jwtAuthenticationToken, @RequestBody @Valid NewReservaDTO reservaMoradorDto) {
        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        // if (!reservaMoradorDto.getNome().equals(user.getUsername())) {
        // return new ResponseEntity<>("Usuário não autorizado",
        // HttpStatus.UNAUTHORIZED);
        // }

        // return new ResponseEntity<>(reservaMoradorDto, HttpStatus.CREATED);

        return new ResponseEntity<>(reservaMoradorService.reservarArea(reservaMoradorDto, user.getUsername()),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MORADOR')")
    @GetMapping("/list")
    public ResponseEntity<?> listAll(JwtAuthenticationToken jwtAuthenticationToken,
            @RequestParam(name = "userName", defaultValue = "", required = false) String userName) {
        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        // if (!userName.equals(user.getUsername()) &&
        // !user.getAuthorities().toString().toUpperCase().contains("ADMIN")) {
        // return new ResponseEntity<>("Usuário não autorizado",
        // HttpStatus.UNAUTHORIZED);
        // }

        if (user.getAuthorities().toString().toUpperCase().contains("ADMIN")) {
            return new ResponseEntity<>(reservaMoradorService.getAllReservas(), HttpStatus.OK);
        } else {

            return new ResponseEntity<>(reservaMoradorService.listAll(user), HttpStatus.OK);
        }

    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MORADOR')")
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable Integer id) {

        User user = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        // if (!user.getAuthorities().toString().toUpperCase().contains("ADMIN")) {
        // return new ResponseEntity<>("Usuário não autorizado",
        // HttpStatus.UNAUTHORIZED);
        // }

        return reservaMoradorService.cancelarReserva(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStatus(@RequestBody @Valid UpdateStatusReservaDTO reservaMoradorJson,
            @PathVariable Integer id,
            JwtAuthenticationToken jwtAuthenticationToken) {

        userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " +
                                jwtAuthenticationToken.getToken().getSubject()));

        // if (!user.getAuthorities().toString().toUpperCase().contains("ADMIN")) {
        // return new ResponseEntity<>("Usuário não autorizado",
        // HttpStatus.UNAUTHORIZED);
        // }

        return reservaMoradorService.updateReserva(id,
                reservaMoradorJson);
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> reserve(@RequestBody @Valid NewReservaDTO reservaMoradorDTO) {

        return new ResponseEntity<>(reservaMoradorDTO, HttpStatus.CREATED);
        // return new ResponseEntity<>(areaService.reservarArea(reservaMoradorDTO),
        // HttpStatus.CREATED);
    }

}
