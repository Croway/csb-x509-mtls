# X509 Camel Rest API Auth

This libraries can be used to configure mutual TLS authentication and authorization via CN for Camel and Spring Boot RESET APIs.

In order to use this library, the following dependency must be imported into your pom.xml

```
<dependency>
    <groupId>com.redhat.camel.springboot.auth</groupId>
    <artifactId>x509</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

The available roles and the users who hold those role can be configured in the application.properties of your application

```
com.redhat.camel.springboot.auth.x509.users.user1=ROLE1,ROLE2
com.redhat.camel.springboot.auth.x509.users.user2=ROLE1
# SSL Configuration
server.ssl.bundle=server
spring.ssl.bundle.jks.server.key.alias=server
spring.ssl.bundle.jks.server.keystore.location=file:/path/to/server.jks
spring.ssl.bundle.jks.server.keystore.password=pass123
spring.ssl.bundle.jks.server.keystore.type=PKCS12
# Two way SSL
server.ssl.client-auth=NEED
spring.ssl.bundle.jks.server.truststore.location=file:/path/to/server-truststore.jks
spring.ssl.bundle.jks.server.truststore.password=pass123
spring.ssl.bundle.jks.server.truststore.type=PKCS12
```

Camel policies can be configured as well, by default, the creation of Camel policies is disabled, but can be enabled via

```
com.redhat.camel.springboot.auth.x509.create-policies=true
```

By default, policies with the following naming convention are created $role+Policy for example, for a role named ADMIN, the following policy is created adminPolicy that can be referenced in your camel routes ala

```
from("direct:x")
    .policy("adminPolicy")
    .setBody(constant("Hello Camel"));
```