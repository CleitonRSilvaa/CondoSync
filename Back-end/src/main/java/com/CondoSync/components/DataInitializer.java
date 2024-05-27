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
            String[] roles = { "ADMIN", "USER", "GUEST", "MORADOR" };
            for (String roleName : roles) {
                if (!roleService.existsByNome(roleName)) {
                    Role newRole = new Role(roleName);
                    roleService.createRole(newRole);
                    log.info("Role {} created", roleName);
                }
            }

            if (!userService.existsByUserName("admin")) {
                User user = new User();
                user.setFullName("CondoSync Admin");
                user.setUserName("admin");
                user.setHashPassword(passwordEncoder.encode("admin123"));
                user.setStatus(true);
                user.setInativa(false);
                Role adminRole = roleService.getRoleByNome("ADMIN");
                user.setRoles(Set.of(adminRole));
                userService.createUser(user);
                log.info("Admin user created");
            }
        } catch (Exception e) {
            log.error("Error initializing data: {}", e.getMessage(), e);
        }
    }

}
