package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.MoradorDTO;
import com.CondoSync.models.DTOs.ResponseDTO;
import com.CondoSync.services.MoradorService;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> listAll() {
        return new ResponseEntity<>(moradorService.listAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/find")
    public ResponseEntity<?> findByEmail(@RequestParam String email) {
        return new ResponseEntity<>(moradorService.findMoradorByEmail(email), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/update/{moradorId}")
    public ResponseEntity<?> update(@RequestBody @Valid MoradorDTO moradorDTO, @PathVariable UUID moradorId) {

        System.out.println("moradorId: " + moradorId);
        System.out.println("moradorDTO.getId(): " + moradorDTO.getId());

        if (!moradorDTO.getId().toString().equals(moradorId.toString())) {

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Id do morador não corresponde ao id do corpo da requisição");
            responseDTO.setError("Id do morador não corresponde ao id do corpo da requisição");
            responseDTO.setStatus(400);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>(moradorService.update(moradorId, moradorDTO), HttpStatus.OK);
    }

    // @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    // @PostMapping("/delete/{moradorId}")
    // public ResponseEntity<?> delete(@PathVariable UUID moradorId) {
    // return new ResponseEntity<>(moradorService.delete(moradorId), HttpStatus.OK);
    // }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/change-status/{moradorId}")
    public ResponseEntity<?> changeStatus(@PathVariable UUID moradorId) {
        return new ResponseEntity<>(moradorService.changeStatus(moradorId), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/find/{moradorId}")
    public ResponseEntity<?> findById(@PathVariable UUID moradorId) {

        var morador = moradorService.findById(moradorId);

        return new ResponseEntity<>(new MoradorDTO(morador), HttpStatus.OK);
    }

}
