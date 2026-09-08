package com.banking.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banking.auth.entity.User;
import com.banking.auth.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername("rohit").isEmpty()) {

                User user = User.builder()
                        .username("rohit")
                        .password(passwordEncoder.encode("password"))
                        .role("USER")
                        .build();

                userRepository.save(user);

                System.out.println("Test user created: rohit");
            }
        };
    }
}