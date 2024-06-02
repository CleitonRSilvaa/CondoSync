package com.CondoSync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.ResponseDTO;
import com.CondoSync.models.DTOs.UsuarioDTO;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    // @GetMapping
    // public List<User> getUsers() {
    // return userService.getUsers();
    // }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> listAll() {
        var users = userService.ListAll();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UsuarioDTO usuarioDTO) {

        var user = userService.register(usuarioDTO);
        if (user == null) {

            var responseDTO = new ResponseDTO();
            responseDTO.setMessage("Erro ao registrar usuário");
            responseDTO.setError("Erro ao registrar usuário");
            responseDTO.setStatus(400);

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(usuarioDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/find/{userId}")
    public ResponseEntity<?> findByEmail(@PathVariable UUID userId) {
        var user = userService.getUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> update(
            @PathVariable UUID userId,
            @RequestBody @Valid UsuarioDTO usuarioDTO) {

        var user = userService.updateUser(userId, usuarioDTO);
        if (user == null) {
            var responseDTO = new ResponseDTO();
            responseDTO.setMessage("Erro ao atualizar usuário");
            responseDTO.setError("Erro ao atualizar usuário");
            responseDTO.setStatus(400);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/change-status/{userId}")
    public ResponseEntity<?> changeStatus(
            @PathVariable UUID userId, JwtAuthenticationToken jwtAuthenticationToken) {

        User userAlth = userService.findByUserName(jwtAuthenticationToken.getToken().getSubject()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + jwtAuthenticationToken.getToken().getSubject()));

        System.out.println("userAlth.getId() " + userAlth.getId());

        if (userAlth.getId().equals(userId)) {
            var responseDTO = new ResponseDTO();
            responseDTO.setMessage("Usuário não pode alterar seu próprio status");
            responseDTO.setError("Usuário não pode alterar seu próprio status");
            responseDTO.setStatus(400);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        var user = userService.changeStatus(userId);
        if (user == null) {
            var responseDTO = new ResponseDTO();
            responseDTO.setMessage("Erro ao atualizar status do usuário");
            responseDTO.setError("Erro ao atualizar status do usuário");
            responseDTO.setStatus(400);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // @GetMapping("/{id}")
    // public User getUserById(@PathVariable UUID id) {
    // return userService.getUserById(id);
    // }

    // @PostMapping
    // public User createUser(@RequestBody @Valid User user) {
    // return userService.createUser(user);
    // }

    // @PutMapping
    // public User updateUser(@RequestBody @Valid User user) {
    // return userService.updateUser(user);
    // }

    // @PatchMapping
    // public User patchUser(@RequestBody @Valid User user) {
    // return userService.patchUser(user);
    // }

    // @DeleteMapping
    // public void deleteUser(@RequestBody @Valid User user) {
    // userService.deleteUser(user);

    // }

}
