package com.redhat.camel.springboot.auth.x509;

import java.util.List;

public class UserCertificate {
    private String name;
    private List<String> roles;

    public UserCertificate(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public UserCertificate() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
