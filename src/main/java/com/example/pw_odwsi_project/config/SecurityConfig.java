package com.example.pw_odwsi_project.config;

import com.example.pw_odwsi_project.auth.CustomAuthenticationProvider;
import com.example.pw_odwsi_project.auth.TotpWebAuthenticationDetailsSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final TotpWebAuthenticationDetailsSource authenticationDetailsSource;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider)
                .build();
    }

    private AuthenticationManager customProviderManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http, List<AuthenticationProvider> providers) throws Exception {
        return http
                .headers(headers -> headers
//                        .addHeaderWriter((request, response) -> {
//                            response.setHeader("Server", "");
//                        })
                        .contentSecurityPolicy(contentSecurityPolicyConfig -> contentSecurityPolicyConfig
                                .policyDirectives("default-src 'self'; img-src * data:; object-src 'none'; upgrade-insecure-requests;"))
                        .xssProtection(xssProtectionConfig -> xssProtectionConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                )
                .cors(withDefaults())
                .csrf(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login", "/register", "/register-qr", "/reset-password", "/change-password").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationManager(customProviderManager(providers))
//                .authenticationProvider(authenticationProvider)
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