# Bet microservices
Several microservices that allows you to bet matches.

## Overview
I am trying to decouple previous monolith implementation. The result should be more matured micro-services architecture of bet application.

## Planned architecture
![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/bet-microservices-architecture-3.png?raw=true)

## Security considerations
There are different security approaches that we could follow in order to implement OAuth2 in bet microservices ecosystem.

I chose to the way where Gateway acts as a OAuth2 Resource Server. Access to the resource requires a valid access token. Token is propagated by API Gateway.

Below I presented example of sequence diagram:
![alt text](https://github.com/PiotrMichalowski96/bet-microservices/blob/master/doc/oauth2-bets-sequence-diagram.png?raw=true)
