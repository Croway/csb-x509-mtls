package com.redhat.camel.springboot.auth.x509;

import org.apache.camel.spring.boot.util.ConditionalOnHierarchicalProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(X509SecurityConfigurationProperties.class)
@ConditionalOnHierarchicalProperties("com.redhat.camel.springboot.auth.x509")
public class X509SecurityAutoConfiguration {

    private final X509SecurityConfigurationProperties properties;

    public X509SecurityAutoConfiguration(X509SecurityConfigurationProperties properties, AuthenticationConfiguration authenticationConfiguration) {
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain x509SecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry.anyRequest().fullyAuthenticated());
        http.x509(httpSecurityX509Configurer ->
                // The regex greps the content of the Common Name in the SSL certificate
                httpSecurityX509Configurer.subjectPrincipalRegex("CN=(.*?)(?:,|$)"));

        return http.build();
    }

    @Bean
    public UserDetailsService configurableInMemoryUserDetailsService() {
        List<UserDetails> users = new ArrayList<>();
        properties.getUsers().forEach((user, roles) -> {
            String[] rolesArray = Arrays.stream(roles.split(",")).map(role -> role.toUpperCase()).toArray(String[]::new);

            users.add(User.withUsername(user).password("not_needed").roles(rolesArray).build());
        });

        return new InMemoryUserDetailsManager(users);
    }
}
