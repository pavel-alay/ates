# Keycloak: Event Listener SPI & Publish events to Kafka

## Setup

Kafka running at port 9092. It could be updated in Producer.java:
`BOOTSTRAP_SERVER = "broker:9092"`

## build 
    `mvn clean install`

## Deploy to Keycloak instance

1. During docker deployment create volume for deployments. Make sure the container have write permissions.
```yml
  volumes:
    - /mnt/docker-base/config/keycloak/deployments:/opt/jboss/keycloak/standalone/deployment
```
2. Copy target/keycloak-spi-kafka.jar to deployments folder:
   `/mnt/docker-base/config/keycloak/deployments`
  
3. Keycloak server will reload it automatically and will create 
`/opt/jboss/keycloak/standalone/deployment/keycloak-spi-kafka.jar.deployed` as a confirmation. 
