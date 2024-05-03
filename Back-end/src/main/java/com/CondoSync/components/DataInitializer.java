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
    public void run(String... args) throws Exception {

        try {

            List<Role> roles = new ArrayList<Role>();

            if (!roleService.existsByNome("ROLE_ADMIN")) {
                var role = new Role("ROLE_ADMIN");
                roleService.createRole(role);
                roles.add(role);

                log.info("Role ROLE_ADMIN created");

            } else {
                roles.add(roleService.getRoleByNome("ROLE_ADMIN"));
            }

            if (!roleService.existsByNome("ROLE_USER"))
                roleService.createRole(new Role("ROLE_USER"));
            if (!roleService.existsByNome("ROLE_GUEST"))
                roleService.createRole(new Role("ROLE_GUEST"));

            if (!userService.existsByUserName("admin")) {
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

        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
        }

    }

}
