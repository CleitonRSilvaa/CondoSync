package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CondoSync.models.User;
import com.CondoSync.repositores.UsersRepository;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // public void createUser(User user) {
    // log.info("Creating user: {}", user.getUsername());
    // try {
    // usersRepository.persist(user);
    // log.info("User created successfully");
    // } catch (PersistenceException pe) {
    // log.error("Failed to create user: {}", pe.getMessage());
    // throw pe;
    // }
    // }

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

}
