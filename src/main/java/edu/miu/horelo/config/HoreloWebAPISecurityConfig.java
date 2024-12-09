package edu.miu.horelo.config;

import edu.miu.horelo.filter.JWTAuthFilter;
import edu.miu.horelo.service.impl.HoreloUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)  // Enable @PreAuthorize and @PostAuthorize
public class HoreloWebAPISecurityConfig {

    private HoreloUserDetailsService horeloUserDetailsService;
    private JWTAuthFilter jwtAuthFilter;

    public HoreloWebAPISecurityConfig(HoreloUserDetailsService horeloUserDetailsService, JWTAuthFilter jwtAuthFilter) {
        this.horeloUserDetailsService = horeloUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_OWNER >ROLE_ADMIN > ROLE_MANAGER > ROLE_SUPPORT > ROLE_USER");
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {
                            auth
                                    .requestMatchers("/public/hello").permitAll()
                                    .requestMatchers("/api/v1/home/**").permitAll()
                                    .requestMatchers("/api/v1/public/auth/hello").permitAll()
                                    .requestMatchers("/api/v1/public/auth/register").permitAll()
                                    .requestMatchers("/api/v1/public/auth/login").permitAll()
                                    //.requestMatchers("/api/v1/public/auth/**").permitAll()
                                    .requestMatchers("/api/v1/public/auth/**").permitAll()
                                    .requestMatchers("/api/v1/public/auth/user").authenticated()
                                    .requestMatchers("/api/v1/public/auth/user/**").authenticated()
                                    //.requestMatchers("/api/v1/public/auth/estore").authenticated()
                                    //.requestMatchers("/api/v1/public/auth/estore/**").authenticated()
                                    //.requestMatchers("/api/v1/public/auth/allergy").authenticated()
                                    //.requestMatchers("/api/v1/public/auth/allergy/**").authenticated()

                                    .requestMatchers("/api/v1/publisher/**")
                                    .authenticated();
//                                    .requestMatchers("/api/v1/publisher/get/**").authenticated();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(horeloUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
