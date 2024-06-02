package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CondoSync.components.ValidateUserException;
import com.CondoSync.models.Role;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.UserUpdatePasswordDTO;
import com.CondoSync.models.DTOs.UsuarioDTO;
import com.CondoSync.repositores.UsersRepository;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return usersRepository.findByuserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public Optional<User> findByUserName(String username) {
        return usersRepository.findByuserName(username);
    }

    public List<User> getUsers() {
        return usersRepository.findAll();
    }

    public User getUserById(UUID id) {
        return usersRepository.findById(id).orElse(null);
    }

    public UsuarioDTO getUser(UUID id) {
        return usersRepository.findById(id).map(UsuarioDTO::new).orElseThrow(
                () -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public User createUser(User user) {
        try {
            var userSave = usersRepository.save(user);
            log.info("User created successfully");
            return userSave;
        } catch (PersistenceException pe) {
            log.error("Failed to create user: {}", pe.getMessage());
            throw pe;
        }

    }

    @Transactional
    public UsuarioDTO register(UsuarioDTO userDto) {

        userDto.validateRolesIds();

        @NotBlank(message = "A senha é obrigatorio")
        @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100 caracteres")
        String senha = userDto.getSenha();

        @NotBlank(message = "A senha de confirmação é obrigatorio")
        @Size(min = 5, max = 100, message = "A senha de confirmação deve ter entre 5 e 100 caracteres")
        String senhaConfirmacao = userDto.getConfirmacaoSenha();

        HashMap<String, Object> dHashMap = new HashMap<>();

        if (!senha.equals(senhaConfirmacao)) {
            throw new IllegalArgumentException("As senhas não conferem");
        }
        if (this.existsByUserName(userDto.getEmail())) {
            dHashMap.put("Email", "Email já cadastrado");
        }

        if (!dHashMap.isEmpty()) {
            throw new ValidateUserException("Um registro com os mesmos dados já existe.", dHashMap);
        }

        Set<Role> roles = new HashSet<Role>();

        for (Long id : userDto.getRolesIds()) {
            roles.add(roleService.getRoleById(id));
        }

        User user = new User();
        user.setFullName(userDto.getNomeCompleto());
        user.setUserName(userDto.getEmail());
        user.setHashPassword(passwordEncoder.encode(userDto.getSenha()));
        user.setStatus(true);
        user.setInativa(false);
        user.setRoles(roles);
        var userSave = usersRepository.save(user);

        userDto.setId(userSave.getId());
        var rolesDto = new HashMap<Long, String>();
        for (Role role : userSave.getRoles()) {
            rolesDto.put(role.getId(), role.getNome());
        }
        userDto.setRoles(rolesDto);
        return userDto;

    }

    @Transactional
    public UsuarioDTO updateUser(UUID usuarioId, UsuarioDTO userDto) {
        try {

            User user = usersRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            findByUserName(userDto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("Email já cadastrado");
                }
            });

            if (!userDto.getSenha().isEmpty() && !userDto.getConfirmacaoSenha().isEmpty()) {

                @NotBlank(message = "A senha é obrigatorio")
                @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100 caracteres")
                String senha = userDto.getSenha();

                @NotBlank(message = "A senha de confirmação é obrigatorio")
                @Size(min = 5, max = 100, message = "A senha de confirmação deve ter entre 5 e 100 caracteres")
                String senhaConfirmacao = userDto.getConfirmacaoSenha();

                if (!senha.equals(senhaConfirmacao)) {
                    throw new IllegalArgumentException("As senhas não conferem");
                }

                user.setHashPassword(passwordEncoder.encode(senha));
                var pass = matchesPassword(senha, user.getHashPassword());
                if (!pass) {
                    throw new IllegalArgumentException("Senha inválida");
                }
            }

            Set<Role> roles = new HashSet<Role>();

            for (Long id : userDto.getRolesIds()) {
                roles.add(roleService.getRoleById(id));
            }

            user.setId(userDto.getId());
            user.setFullName(userDto.getNomeCompleto());
            user.setUserName(userDto.getEmail());
            user.setHashPassword(passwordEncoder.encode(userDto.getSenha()));

            user.setRoles(roles);
            var userSave = updateUser(user);
            userDto.setId(userSave.getId());

            var rolesDto = new HashMap<Long, String>();

            for (Role role : userSave.getRoles()) {
                rolesDto.put(role.getId(), role.getNome());
            }

            userDto.setRoles(rolesDto);
            return userDto;
        } catch (PersistenceException pe) {
            log.error("Failed to create user: {}", pe.getMessage());
            throw pe;
        }

    }

    @Transactional
    public User updateUser(User user) {
        try {
            var userSave = usersRepository.save(user);
            log.info("User updated successfully");
            return userSave;
        } catch (PersistenceException pe) {
            log.error("Failed to update user: {}", pe.getMessage());
            throw pe;
        }

    }

    @Transactional
    public User patchUser(User user) {
        try {
            var userSave = usersRepository.save(user);
            log.info("User updated successfully");
            return userSave;
        } catch (PersistenceException pe) {
            log.error("Failed to update user: {}", pe.getMessage());
            throw pe;
        }

    }

    @Transactional
    public void deleteUser(User user) {
        usersRepository.delete(user);
    }

    public boolean existsByUserName(String string) {
        return usersRepository.existsByuserName(string);
    }

    public Optional<User> findById(UUID id) {
        return usersRepository.findById(id);
    }

    public String encodePassword(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean matchesPassword(String senha, String hashSenha) {
        return passwordEncoder.matches(senha, hashSenha);
    }

    @Transactional
    public ResponseEntity<?> changeStatus(UUID userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        user.setStatus(!user.isStatus());
        updateUser(user);
        return ResponseEntity.ok().build();
    }

    public List<UsuarioDTO> ListAll() {
        return usersRepository.findAllAdmins().stream().map(UsuarioDTO::new)
                .sorted(
                        (m1, m2) -> m1.getNomeCompleto().compareTo(m2.getNomeCompleto()))
                .collect(Collectors.toList());

    }

    public UsuarioDTO findByEmail(String email) {

        User user = usersRepository.findByuserName(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UsuarioDTO userDto = new UsuarioDTO();
        userDto.setId(user.getId());
        userDto.setEmail(user.getUsername());
        userDto.setNomeCompleto(user.getFullName());
        userDto.setStatus(user.isStatus());

        Set<Long> rolesIds = new HashSet<Long>();
        for (Role role : user.getRoles()) {
            rolesIds.add(role.getId());
        }
        userDto.setRolesIds(rolesIds);
        var roles = new HashMap<Long, String>();
        for (Role role : user.getRoles()) {
            roles.put(role.getId(), role.getNome());
        }
        userDto.setRoles(roles);
        return userDto;
    }

    public char[] generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] password = new char[8];
        for (int i = 0; i < 8; i++) {
            int index = (int) (chars.length() * Math.random());
            password[i] = chars.charAt(index);
        }
        return password;
    }

    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordDTO userUpdatePasswordDTO) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!userId.equals(user.getId())) {
            throw new IllegalArgumentException("Usuário não pode alterar a senha de outro usuário");
        }

        if (!matchesPassword(userUpdatePasswordDTO.getSenhaAtual(), user.getHashPassword())) {
            throw new IllegalArgumentException("Senha atual inválida");
        }

        if (matchesPassword(userUpdatePasswordDTO.getNovaSenha(), user.getHashPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual a senha atual");
        }

        if (!userUpdatePasswordDTO.getNovaSenha().equals(userUpdatePasswordDTO
                .getConfirmacaoNovaSenha())) {
            throw new IllegalArgumentException("As senhas não conferem");
        }

        user.setHashPassword(passwordEncoder.encode(userUpdatePasswordDTO.getNovaSenha()));

        user.setDatahashSenhaUpdate(
                LocalDateTime.now().plusDays(30));
        updateUser(user);

    }

}
