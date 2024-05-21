package com.redhat.camel.springboot.auth.x509;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "com.redhat.camel.springboot.auth.x509")
public class X509SecurityConfigurationProperties {

    private final Map<String, String> users = new HashMap<>();

    public Map<String, String> getUsers() {
        return users;
    }
}
