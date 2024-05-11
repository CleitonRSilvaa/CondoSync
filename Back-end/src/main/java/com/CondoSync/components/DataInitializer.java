package com.CondoSync.components;

import java.util.ArrayList;
import java.util.List;
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

            if (!roleService.existsByNome("ROLE_ADMIN")) {
                roleService.createRole(new Role("ROLE_ADMIN"));
                log.info("Role ROLE_ADMIN created");
            }
            if (!roleService.existsByNome("ROLE_USER")) {
                roleService.createRole(new Role("ROLE_USER"));
                log.info("Role ROLE_USER created");
            }
            if (!roleService.existsByNome("ROLE_GUEST")) {
                roleService.createRole(new Role("ROLE_GUEST"));
                log.info("Role ROLE_GUEST created");
            }
            if (!roleService.existsByNome("ROLE_MORADOR")) {
                roleService.createRole(new Role("ROLE_MORADOR"));
                log.info("Role ROLE_MORADOR created");
            }

            if (!userService.existsByUserName("admin")) {

                List<Role> roles = new ArrayList<Role>();
                roles.add(roleService.getRoleByNome("ROLE_ADMIN"));
                User user = new User();
                user.setFullName("CondoSync Admin");
                user.setUserName("admin");
                user.setHashPassword(passwordEncoder.encode("admin123"));
                user.setStatus(true);
                user.setInativa(false);
                user.setRoles(roles);
                userService.createUser(user);
                log.info("Admin user created");
            }

        } catch (ValidateUserException e) {
            log.info("Error: " + e.getMessage());
        }

    }

}
