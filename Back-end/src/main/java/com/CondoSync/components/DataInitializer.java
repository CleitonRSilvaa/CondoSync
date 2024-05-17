package com.CondoSync.components;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.CondoSync.models.Role;
import com.CondoSync.models.User;
import com.CondoSync.services.RoleService;
import com.CondoSync.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws ValidateUserException {

        try {

            if (!roleService.existsByNome("ADMIN")) {
                roleService.createRole(new Role("ADMIN"));
                log.info("Role ADMIN created");
            }
            if (!roleService.existsByNome("USER")) {
                roleService.createRole(new Role("USER"));
                log.info("Role USER created");
            }
            if (!roleService.existsByNome("GUEST")) {
                roleService.createRole(new Role("GUEST"));
                log.info("Role GUEST created");
            }
            if (!roleService.existsByNome("MORADOR")) {
                roleService.createRole(new Role("MORADOR"));
                log.info("Role MORADOR created");
            }

            if (!userService.existsByUserName("admin")) {
                User user = new User();
                user.setFullName("CondoSync Admin");
                user.setUserName("admin");
                user.setHashPassword(passwordEncoder.encode("admin123"));
                user.setStatus(true);
                user.setInativa(false);
                user.setRoles(Set.of(roleService.getRoleByNome("ADMIN")));
                userService.createUser(user);
                log.info("Admin user created");
            }

        } catch (ValidateUserException e) {
            log.info("Error: " + e.getMessage());
        }

    }

}
