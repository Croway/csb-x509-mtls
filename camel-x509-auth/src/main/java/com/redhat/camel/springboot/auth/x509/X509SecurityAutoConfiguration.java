package com.redhat.camel.springboot.auth.x509;

import org.apache.camel.spring.boot.util.ConditionalOnHierarchicalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(X509SecurityConfigurationProperties.class)
@ConditionalOnHierarchicalProperties("com.redhat.camel.springboot.auth.x509")
@ConditionalOnProperty(name = "com.redhat.camel.springboot.auth.x509.enabled", havingValue = "true")
public class X509SecurityAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(X509SecurityAutoConfiguration.class);

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
                httpSecurityX509Configurer.subjectPrincipalRegex(properties.getSubjectPrincipalRegex()));

        LOG.info("Configuring X-509 Security FilterChain");
        return http.build();
    }

    @Bean
    public UserDetailsService configurableInMemoryUserDetailsService() {
        List<UserDetails> users = new ArrayList<>(properties.getUsers().size());
        properties.getUsers().forEach((user, roles) -> {
            String[] rolesArray = roles.stream().map(role -> role.toUpperCase().trim()).toArray(String[]::new);

            LOG.debug("Creating user '{}' with roles {}", user, rolesArray);
            users.add(User.withUsername(user).password(properties.getUnusedPassword()).roles(rolesArray).build());
        });

        return new InMemoryUserDetailsManager(users);
    }
}
