package com.CondoSync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.DTOs.ReservaMoradorDTO;
import com.CondoSync.services.AreaService;
import com.CondoSync.services.HorarioService;
import com.CondoSync.services.ReservaMoradorService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid ReservaMoradorDTO reservaMoradorDto) {
        return new ResponseEntity<>(areaService.reservarArea(reservaMoradorDto), HttpStatus.CREATED);
    }

    // @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USUARIO')")
    @GetMapping("/list")
    public ResponseEntity<?> listAll(@AuthenticationPrincipal UserDetails userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.err.println(userDetails.getUsername());
        System.out.println(authentication.getName());
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
