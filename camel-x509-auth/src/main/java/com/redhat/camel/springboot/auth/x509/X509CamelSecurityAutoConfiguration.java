package com.redhat.camel.springboot.auth.x509;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.camel.spring.boot.util.ConditionalOnHierarchicalProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(X509SecurityConfigurationProperties.class)
@ConditionalOnHierarchicalProperties("com.redhat.camel.springboot.auth.x509")
@AutoConfigureAfter(X509SecurityAutoConfiguration.class)
public class X509CamelSecurityAutoConfiguration {

    private final X509SecurityConfigurationProperties properties;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ApplicationContext applicationContext;

    public X509CamelSecurityAutoConfiguration(X509SecurityConfigurationProperties properties,
                                              AuthenticationConfiguration authenticationConfiguration,
                                              ApplicationContext applicationContext) {
        this.properties = properties;
        this.authenticationConfiguration = authenticationConfiguration;
        this.applicationContext = applicationContext;
    }

    @Bean
    public List<AuthorizationPolicy> policies() {
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();

        Set<String> roles = properties.getUsers().values().stream()
                .map(rolesString -> rolesString.split(","))
                .flatMap(list -> Arrays.stream(list))
                .collect(Collectors.toSet());

        roles.forEach(role -> {
            // generate a Camel Policy foreach role
            SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();

            List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
            decisionVoters.add(new RoleVoter());
            try {
                policy.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            policy.setAccessDecisionManager(new AffirmativeBased(decisionVoters));
            policy.setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy("ROLE_" + role));

            beanFactory.registerSingleton(role.toLowerCase(Locale.ROOT) + "Policy", policy);
        });

        // beans are registered programmatically
        return null;
    }
}
