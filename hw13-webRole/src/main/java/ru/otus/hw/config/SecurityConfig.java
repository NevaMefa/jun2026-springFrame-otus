package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    configurePublic(authorize);
                    configureRead(authorize);
                    configureWrite(authorize);
                    configureComments(authorize);
                    authorize.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                );

        return http.build();
    }

    private void configurePublic(
            org.springframework.security.config.annotation.web.configurers
                    .AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers("/login", "/css/**", "/js/**", "/index.html")
                .permitAll();
    }

    private void configureRead(
            org.springframework.security.config.annotation.web.configurers
                    .AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers(HttpMethod.GET, "/api/v1/book", "/api/v1/book/**")
                .hasAnyRole("USER", "ADMIN");
        authorize.requestMatchers(HttpMethod.GET, "/api/v1/author", "/api/v1/author/**")
                .hasAnyRole("USER", "ADMIN");
        authorize.requestMatchers(HttpMethod.GET, "/api/v1/genre", "/api/v1/genre/**")
                .hasAnyRole("USER", "ADMIN");
        authorize.requestMatchers(HttpMethod.GET, "/api/v1/comment", "/api/v1/comment/**")
                .hasAnyRole("USER", "ADMIN");
    }

    private void configureWrite(
            org.springframework.security.config.annotation.web.configurers
                    .AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers(HttpMethod.POST, "/api/v1/book")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, "/api/v1/book/**")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/book/**")
                .hasRole("ADMIN");

        authorize.requestMatchers(HttpMethod.POST, "/api/v1/author")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, "/api/v1/author/**")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/author/**")
                .hasRole("ADMIN");

        authorize.requestMatchers(HttpMethod.POST, "/api/v1/genre")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, "/api/v1/genre/**")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/genre/**")
                .hasRole("ADMIN");
    }

    private void configureComments(
            org.springframework.security.config.annotation.web.configurers
                    .AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers(HttpMethod.POST, "/api/v1/comment")
                .hasAnyRole("USER", "ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, "/api/v1/comment/**")
                .hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/comment/**")
                .hasRole("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}