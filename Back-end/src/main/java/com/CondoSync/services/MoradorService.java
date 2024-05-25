package com.CondoSync.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CondoSync.components.ValidateUserException;
import com.CondoSync.models.Morador;
import com.CondoSync.models.Role;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.MoradorDTO;
import com.CondoSync.repositores.MoradorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public Morador findMoradorByEmail(String email) {
        return moradorRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Morador não encontrado com o email: " + email));
    }

    public MoradorDTO findProfiler(String email) {
        var morador = findMoradorByEmail(email);
        return new MoradorDTO(morador);
    }

    public Morador save(Morador morador) {
        return moradorRepository.save(morador);
    }

    @Transactional
    public Morador register(MoradorDTO moradorDTO) {

        moradorDTO.validateRolesIds();

        @NotBlank(message = "A senha é obrigatorio")
        @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100 caracteres")
        String senha = moradorDTO.getSenha();

        @NotBlank(message = "A senha de confirmação é obrigatorio")
        @Size(min = 5, max = 100, message = "A senha de confirmação deve ter entre 5 e 100 caracteres")
        String senhaConfirmacao = moradorDTO.getConfirmacaoSenha();

        if (!senha.equals(senhaConfirmacao)) {
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

        User user = new User();
        Set<Role> roles = new HashSet<Role>();

        for (Long id : moradorDTO.getRolesIds()) {
            roles.add(roleService.getRoleById(id));
        }

        user.setFullName(moradorDTO.getNomeCompleto());
        user.setUserName(moradorDTO.getEmail());
        user.setHashPassword(userService.encodePassword(moradorDTO.getSenha()));

        user.setStatus(true);
        user.setInativa(false);
        user.setRoles(roles);

        Morador morador = new Morador(moradorDTO);
        morador.setAtivo(true);
        morador.setUser(user);

        // userService.createUser(user);

        return moradorRepository.save(morador);
    }

    public Morador findById(UUID moradorId) {
        return moradorRepository.findById(moradorId)
                .orElseThrow(() -> new EntityNotFoundException("Morador não encontrado"));

    }

    public List<MoradorDTO> listAll() {

        return moradorRepository.findAll().stream()
                .map(morador -> new MoradorDTO(morador))
                .sorted((m1, m2) -> m1.getNomeCompleto().compareTo(m2.getNomeCompleto()))
                .collect(Collectors.toList());

    }

    @Transactional
    public MoradorDTO update(UUID moradorId, MoradorDTO moradorDTO) {
        Morador morador = findById(moradorId);

        if (findByCpf(moradorDTO.getCpf()).isPresent() && !moradorDTO.getCpf().equals(morador.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado por outro morador");
        }

        if (findByEmail(moradorDTO.getEmail()).isPresent() && !moradorDTO.getEmail().equals(morador.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado por outro morador");
        }

        if (!moradorDTO.getSenha().isEmpty() && !moradorDTO.getConfirmacaoSenha().isEmpty()) {
            if (!moradorDTO.getSenha().equals(moradorDTO.getConfirmacaoSenha())) {
                throw new IllegalArgumentException("As senhas não conferem");
            }
            if (moradorDTO.getSenha().length() < 5 || moradorDTO.getSenha().length() > 100) {
                throw new IllegalArgumentException("A senha deve ter entre 5 e 100 caracteres");
            }
            if (moradorDTO.getConfirmacaoSenha().length() < 5 || moradorDTO.getConfirmacaoSenha().length() > 100) {
                throw new IllegalArgumentException("A senha de confirmação deve ter entre 5 e 100 caracteres");
            }
            User user = morador.getUser();
            user.setHashPassword(userService.encodePassword(moradorDTO.getSenha()));
            var pass = userService.matchesPassword(moradorDTO.getSenha(), user.getHashPassword());
            if (!pass) {
                throw new IllegalArgumentException("Senha inválida");
            }
            userService.updateUser(user);

        }

        morador.setCpf(moradorDTO.getCpf());
        morador.setBloco(moradorDTO.getBloco());
        morador.setApartamento(moradorDTO.getApartamento());
        morador.setCelular(moradorDTO.getCelular());
        morador.setEmail(moradorDTO.getEmail());
        morador.setNome(moradorDTO.getNomeCompleto());
        morador.setDataNascimento(moradorDTO.getDataNascimento());
        morador.setTorre(moradorDTO.getTorre());
        morador.setRg(moradorDTO.getRg());

        return new MoradorDTO(moradorRepository.save(morador));
    }

    @Transactional
    public ResponseEntity<?> delete(UUID moradorId) {
        Morador morador = findById(moradorId);
        moradorRepository.delete(morador);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> changeStatus(UUID moradorId) {
        Morador morador = findById(moradorId);
        User user = morador.getUser();
        System.out.println(user);

        user.setStatus(!user.isStatus());
        morador.setAtivo(!morador.isAtivo());
        moradorRepository.save(morador);
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

}
