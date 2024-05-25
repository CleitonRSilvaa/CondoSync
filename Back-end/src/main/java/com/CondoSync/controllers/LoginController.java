package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;
import com.CondoSync.services.TokenService;

import jakarta.validation.Valid;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/v1/login")
@Validated
public class LoginController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid Credencial usuarioCred) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        usuarioCred.email(), usuarioCred.password()));
        User usuario = (User) auth.getPrincipal();
        String jwt = tokenService.generateToken(usuario, Duration.ofMinutes(60));
        return ResponseEntity.ok(new JwtKey(jwt));

    }

    public static record Credencial(String email, String password) {

    }

    public static record JwtKey(String token) {
    }

}
