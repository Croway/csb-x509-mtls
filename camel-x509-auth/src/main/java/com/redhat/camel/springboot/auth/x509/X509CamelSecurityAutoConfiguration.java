package com.redhat.camel.springboot.auth.x509;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.camel.spring.boot.util.ConditionalOnHierarchicalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = {"com.redhat.camel.springboot.auth.x509.create-policies",
        "com.redhat.camel.springboot.auth.x509.enabled"}, havingValue = "true")
@AutoConfigureAfter(X509SecurityAutoConfiguration.class)
public class X509CamelSecurityAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(X509CamelSecurityAutoConfiguration.class);

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
                .flatMap(list -> list.getRoles().stream())
                .collect(Collectors.toSet());

        roles.forEach(role -> {
            // generate a Camel Policy foreach role
            SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();

            List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
            decisionVoters.add(new RoleVoter());
            try {
                policy.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
            } catch (Exception e) {
                LOG.error("Error setting authentication manager", e);
                throw new RuntimeException(e);
            }
            policy.setAccessDecisionManager(new AffirmativeBased(decisionVoters));
            String accessName = properties.getSpringSecurityRolePrefix() + role;
            policy.setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy(accessName));
            LOG.debug("Created Spring Security Access Policy with access '{}'", accessName);

            String beanName = role.toLowerCase(Locale.ROOT) + properties.getPolicySuffix();
            beanFactory.registerSingleton(beanName, policy);
            LOG.debug("Added policy for role '{}', with name '{}'", role, beanName);
        });

        // beans are registered programmatically
        return null;
    }
}
