== Camel Spring Boot Platform HTTP with mTLS and CN Authentication

The following example is built on top of https://github.com/apache/camel-spring-boot-examples/tree/main/http-ssl

You can run it executing the server and client

`cd client && mvn spring-boot:run`

`cd server && mvn spring-boot:run`

The client expose an authenticated REST API that you can use to start the interaction between the services

`curl localhost:8080/ping`

The client, behind the hood, will configure the SSL certificate and will invoke the server on `https://localhost:8443/ping`

The X509 security configuration (`SecurityConfiguration` in the server app) will act as an authentication filter, in particular, if the SSL certificate is valid it will check the CN content, and will try to execute an authentication, as you can see from the userDetailsService an in memory User Store is created by default with one user named localhost (that is the actual CN of the certificate). By changing the SecurityConfiguration, in particular the username from localhost to any other string the `curl localhost:8080/ping` will fail, the SSL certificate is valid, but the CN is not Authorized to execute the request (you can check the client logs for the actual error, that should be 403).