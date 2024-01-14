package com.example.pw_odwsi_project.config;

import com.example.pw_odwsi_project.auth.totp.CustomAuthenticationProvider;
import com.example.pw_odwsi_project.auth.totp.CustomWebAuthenticationDetailsSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor()
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login", "/register", "/register-qr", "/reset-password", "/change-password").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
//                .requiresChannel(requiresChannel -> requiresChannel
//                        .anyRequest().requiresSecure())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .failureUrl("/login?loginError=true")
                        .authenticationDetailsSource(authenticationDetailsSource)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logoutSuccess=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .build();
    }

}