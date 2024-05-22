# Camel Spring Boot Platform HTTP with mTLS and CN Authentication

The following example is built on top of https://github.com/apache/camel-spring-boot-examples/tree/main/http-ssl

You can run it executing the server and client

`cd camel-x509-auth && mvn install`

`cd client && mvn spring-boot:run`

`cd server && mvn spring-boot:run`

The client expose an authenticated REST API that you can use to start the interaction between the services

`curl localhost:8080/ping`

The client, behind the hood, will configure the SSL certificate and will invoke the server on `https://localhost:8443/ping`