package com.redhat.camel.springboot.example.httpssl;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.apache.camel.spi.AuthorizationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry.anyRequest().fullyAuthenticated());
        http.x509(httpSecurityX509Configurer ->
                // The regex greps the content of the Common Name in the SSL certificate
                httpSecurityX509Configurer.subjectPrincipalRegex("CN=(.*?)(?:,|$)"));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                // In this example, the Common Name of the certificate is localhost
                // you can change it to another value, and the server will reply with an error (403)
                User.withUsername("localhost")
                        // The password is not needed by mutual TLS auth, but Spring Boot UserDetailsService need it
                        .password("not_needed")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SpringSecurityAuthorizationPolicy authorizationPolicy(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();

        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());

        policy.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        policy.setAccessDecisionManager(new AffirmativeBased(decisionVoters));
        policy. setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy("ROLE_USER"));

        return policy;
    }
}
