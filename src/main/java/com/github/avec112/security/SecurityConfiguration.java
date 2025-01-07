package com.github.avec112.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {



    private final UserDetailsServiceImpl userDetailsService; // Inject UserDetailsServiceImpl

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService; // Constructor injection
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

        // Icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());

        // Add stateless remember-me functionality
        http.rememberMe(remember -> remember
                .key("yourUniqueRememberMeKey") // Security key for encrypting remember-me cookie
                .alwaysRemember(true) // Automatically enable remember-me for all logins by default
                .tokenValiditySeconds(3600*24*30) // 30 days
                .userDetailsService(userDetailsService)); // Use your custom UserDetailsService for authentication

        super.configure(http);
        setLoginView(http, "/login");
    }

}
