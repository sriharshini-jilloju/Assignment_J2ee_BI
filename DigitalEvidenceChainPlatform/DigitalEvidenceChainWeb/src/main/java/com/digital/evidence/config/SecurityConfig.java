package com.digital.evidence.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// + OAuth2 client imports
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.digital.evidence.auth.AppUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final AppUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/login", "/login", "/users/create",
                    "/css/**", "/js/**", "/webjars/**",
                    // allow OAuth2 endpoints for SSO
                    "/oauth2/**", "/login/oauth2/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .anyRequest().authenticated()
            )
            // ---- BASIC (form) LOGIN stays as-is ----
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/evidence/all", true)
                .permitAll()
            )
            // ---- ADD KEYCLOAK SSO (OAuth2 login) ----
            .oauth2Login(o -> o
                .loginPage("/login") 
                .defaultSuccessUrl("/evidence/all", true)
                .failureHandler((req, res, ex) -> {
                    ex.printStackTrace(); // logs full cause in server console
                    String msg = java.net.URLEncoder.encode(ex.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
                    res.sendRedirect("/auth/login?oauth2_error=" + msg);
                })
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login")
                .permitAll()
            )
            .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }

    // ---- OAuth2 client registration (non-Boot: define beans) ----
    private static final String OIDC = "http://localhost:3128/realms/digital-evidence/protocol/openid-connect";

    @Bean
    public ClientRegistration keycloakRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
            .clientId("evidence-api-client")          
            .clientSecret("odDA7mTik93Zp4fhZkHsOyvnt62nQoUa")           
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid")
            .authorizationUri(OIDC + "/auth")
            .tokenUri(OIDC + "/token")
            .userInfoUri(OIDC + "/userinfo")
            .jwkSetUri(OIDC + "/certs")
            .clientName("Keycloak")
            .userNameAttributeName("sub")
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration kc) {
        return new InMemoryClientRegistrationRepository(kc);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
        return new InMemoryOAuth2AuthorizedClientService(repo);
    }

    // ---- existing basic auth provider ----
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
