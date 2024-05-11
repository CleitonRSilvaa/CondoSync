package com.CondoSync.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.CondoSync.components.ValidateUserException;
import com.CondoSync.models.Morador;
import com.CondoSync.models.Role;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.MoradorDTO;
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

    public Morador register(MoradorDTO moradorDTO) {

        moradorDTO.validateRolesIds();
        if (!moradorDTO.isSenhaValida()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }

        HashMap<String, Object> dHashMap = new HashMap<>();

        if (findByCpf(moradorDTO.getCpf()).isPresent()) {
            dHashMap.put("CPF", "CPF já cadastrado");
        }
        if (findByEmail(moradorDTO.getEmail()).isPresent() || userService.existsByUserName(moradorDTO.getEmail())) {
            dHashMap.put("Email", "Email já cadastrado");
        }

        if (!dHashMap.isEmpty()) {
            throw new ValidateUserException("Um registro com os mesmos dados já existe.", dHashMap);
        }

        Morador morador = new Morador();
        User user = new User();
        List<Role> roles = new ArrayList<Role>();

        for (Long id : moradorDTO.getRolesIds()) {
            roles.add(roleService.getRoleById(id));
        }

        user.setFullName(moradorDTO.getNomeCompleto());
        user.setUserName(moradorDTO.getEmail());
        user.setHashPassword(userService.encodePassword(moradorDTO.getSenha()));
        user.setStatus(true);
        user.setInativa(false);
        user.setRoles(roles);
        userService.createUser(user);

        morador.setCpf(moradorDTO.getCpf());
        morador.setBloco(moradorDTO.getBloco());
        morador.setApartamento(moradorDTO.getApartamento());
        morador.setCelular(moradorDTO.getCelular());
        morador.setEmail(moradorDTO.getEmail());
        morador.setNome(moradorDTO.getNomeCompleto());
        morador.setUser(user);
        return moradorRepository.save(morador);
    }

}
