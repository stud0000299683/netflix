package ru.utmn.chamortsev.netflix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.utmn.chamortsev.netflix.security.JpaUserDetailsService;
import ru.utmn.chamortsev.netflix.security.PersonRepository;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Profile({"CsvEngine", "JdbcEngine"})
    public UserDetailsService userDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withUsername("user")
                        .password(bCryptPasswordEncoder.encode("userPass"))
                        .roles("USER")
                        .build());
        manager.createUser(
                User.withUsername("admin")
                        .password(bCryptPasswordEncoder.encode("adminPass"))
                        .roles("ADMIN")
                        .build());
        return manager;
    }

    @Bean
    @Profile("JpaEngine")
    public UserDetailsService userDetailsService2(
            PersonRepository personRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new JpaUserDetailsService(personRepository, bCryptPasswordEncoder);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable())
                .authorizeHttpRequests(authorizationRegistry ->
                        authorizationRegistry
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/login/**").permitAll()
                                .requestMatchers(toH2Console()).permitAll()
                                .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}