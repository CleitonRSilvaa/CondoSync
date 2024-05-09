package com.CondoSync.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Morador;
import com.CondoSync.repositores.MoradorRepository;

@Service
public class MoradorService {

    @Autowired
    private MoradorRepository moradorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public Optional<Morador> findByCpf(String cpf) {
        return moradorRepository.findByCpf(cpf);
    }

    public Optional<Morador> findByEmail(String email) {
        return moradorRepository.findByEmail(email);
    }

    public Morador save(Morador morador) {
        return moradorRepository.save(morador);
    }

}
