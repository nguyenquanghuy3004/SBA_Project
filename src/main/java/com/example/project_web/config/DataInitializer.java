package com.example.project_web.config;

import com.example.project_web.entity.Role;
import com.example.project_web.entity.User;
import com.example.project_web.enums.RoleName;
import com.example.project_web.repository.RoleRepository;
import com.example.project_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(new Role(roleName));
            }
        });


        User admin = userRepository.findByUsername("admin").orElse(null);
        Role adminRole = roleRepository.findByName(RoleName.ADMIN).get();

        if (admin == null) {
            admin = new User("admin", "admin@gmail.com", passwordEncoder.encode("admin123"));
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(admin);
        } else {

            if (!admin.getRoles().contains(adminRole)) {
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                userRepository.save(admin);
            }
        }
    }
}
