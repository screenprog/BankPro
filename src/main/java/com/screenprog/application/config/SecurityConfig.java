package com.screenprog.application.config;

import com.screenprog.application.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("admin/register", "user/register",
                                "user/login")
                        .permitAll()
                        .requestMatchers("admin/hello", "admin/add-one-staff",
                                "admin/get-staff/{id}",
                                "admin/get-all-staff")
                        .hasRole("ADMIN")
                        .requestMatchers("staff/add-one-account",
                                "staff/get-account{id}", "staff/get-all-accounts",
                                "staff/add-one-customer", "staff/get-customer/{id}",
                                "staff/get-all-customers", "staff/deposit/{accountId}",
                                "staff/withdraw/{accountId}",
                                "staff/get-pending-application",
                                "staff/update-application")
                        .hasRole("STAFF")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build(); //to build and return an object of DefaultSecurityFilterChain
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(this.userDetailsService);
        return provider;
    }
}
