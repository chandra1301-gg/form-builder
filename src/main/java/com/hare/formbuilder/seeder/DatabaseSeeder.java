package com.hare.formbuilder.seeder;

import com.hare.formbuilder.entity.*;
import com.hare.formbuilder.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .name("User 1")
                    .email("user1@webtech.id")
                    .password(passwordEncoder.encode("password1"))
                    .build();

            User user2 = User.builder()
                    .name("User 2")
                    .email("user2@webtech.id")
                    .password(passwordEncoder.encode("password2"))
                    .build();

            User user3 = User.builder()
                    .name("User 3")
                    .email("user3@worldskills.org")
                    .password(passwordEncoder.encode("password3"))
                    .build();

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            System.out.println("========================================");
            System.out.println("  DATABASE SEEDER RUNNING...");
            System.out.println("  Users seeded:");
            System.out.println("  - user1@webtech.id / password1");
            System.out.println("  - user2@webtech.id / password2");
            System.out.println("  - user3@worldskills.org / password3");
            System.out.println("========================================");
        }
    }
}