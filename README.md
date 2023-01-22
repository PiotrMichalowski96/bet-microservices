# Bet microservices
Several microservices that is used for Bet League. It allows you to bet matches, score points for correct results and compare with your friends.

## Requirement
Use case chart shows the main requirement for Bet League:
![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/bet-microservices-requirements.png?raw=true)

## Project Overview
It was a monolith implementation in the past. I used this project to do a decoupling of monolith. It was a process of changing to micro-services architecture.

Technology stack for backend services:
- Java 17
- Spring Cloud
- Apache Kafka
- MongoDB

Testing:
- Test containers
- Cucumber (Behaviour-Driven Development)
- Spring Cloud Contract Tests (Consumer-Driven Contract)
- JUnit 5

Security:
- OAuth2
- Spring Security OAuth2.0.
- Keycloak

Technology stack for frontend:
- TypeScript
- Angular

## Architecture
![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/bet-microservices-architecture-3.png?raw=true)

## Testing
- There are unit tests with JUnit 5 for all microservices.
- In Match API there are also integration tests. I used test containers and Cucumber for them.
- Most complex business logic is present in Bet Event Aggregator, Bet API and Match API services.
- For those three most important microservices I prepared a code coverage report to ensure coverage is more than 80%:
- ![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/bet-microservices-code-coverage.png?raw=true)

## Security considerations
There are different security approaches that we could follow in order to implement OAuth2 in bet microservices ecosystem.

I chose to the way where Gateway acts as a OAuth2 Resource Server. Access to the resource requires a valid access token. Token is propagated by API Gateway.

Below I presented example of sequence diagram:
![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/oauth2-bets-sequence-diagram.png?raw=true)

## Setup
To run this project, build it locally with Maven and additionally create a docker image of each microservice:
```
$ mvn clean package docker:build
```
Then you can run all microservices containers with docker compose file:
```
$ docker-compose -f "<project-path>\doc\docker\bet-services-compose.yaml"
```
All of microservices in docker containers could be also started on cloud environment.