package com.redhat.camel.springboot.auth.x509;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "com.redhat.camel.springboot.auth.x509")
public class X509SecurityConfigurationProperties {

    private Boolean enabled = Boolean.FALSE;
    private final Map<String, List<String>> users = new HashMap<>();
    private String subjectPrincipalRegex = "CN=(.*?)(?:,|$)";
    private String unusedPassword = "unused";
    private String policySuffix = "Policy";
    private String springSecurityRolePrefix = "ROLE_";
    private Boolean createPolicies = Boolean.FALSE;

    public Map<String, List<String>> getUsers() {
        return users;
    }

    public String getSubjectPrincipalRegex() {
        return subjectPrincipalRegex;
    }

    public String getUnusedPassword() {
        return unusedPassword;
    }

    public String getPolicySuffix() {
        return policySuffix;
    }

    public String getSpringSecurityRolePrefix() {
        return springSecurityRolePrefix;
    }

    public Boolean getCreatePolicies() {
        return createPolicies;
    }

    public void setCreatePolicies(Boolean createPolicies) {
        this.createPolicies = createPolicies;
    }

    public void setSubjectPrincipalRegex(String subjectPrincipalRegex) {
        this.subjectPrincipalRegex = subjectPrincipalRegex;
    }

    public void setUnusedPassword(String unusedPassword) {
        this.unusedPassword = unusedPassword;
    }

    public void setPolicySuffix(String policySuffix) {
        this.policySuffix = policySuffix;
    }

    public void setSpringSecurityRolePrefix(String springSecurityRolePrefix) {
        this.springSecurityRolePrefix = springSecurityRolePrefix;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
