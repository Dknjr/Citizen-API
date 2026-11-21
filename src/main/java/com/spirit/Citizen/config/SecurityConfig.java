package com.spirit.Citizen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password("{noop}password") // {noop} est utilisé pour indiquer que le mot de passe n'est pas encodé
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build());
        return manager;
    }



}
