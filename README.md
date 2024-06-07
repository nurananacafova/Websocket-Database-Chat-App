# Chat app

This is chat app with Websocket and Apache Kafka

## Table of Contents

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Join and Send Public Message](#join-and-send-public-message)
4. [Send Private Message](#send-private-message)
5. [Built With](#built-with)

## Installation

```
git clone https://github.com/nurananacafova/Websocket-Chat-App.git
```

## Getting Started

* Run docker-compose.yml for Kafka:
```
docker-compose up
```

* Run the project. Open the below link in the browser:

```
http://localhost:8080
```
## Join and Send Public Message
* For join and send public message from Kafka, do this in http://localhost:8080/send:

Join chat:
```
{
    "status":"JOIN",
    "senderName":"yourusername"
}
```
Send message to the chat:
```
{
    "status":"CHAT",
    "message": "yourmessage",
    "senderName":"yourusername"
}
```
Leave the chat:
```
{
    "status":"LEAVE",
    "senderName":"yourusername"
}
```
## Send Private Message
* For send private message to specific user from Kafka, do this in http://localhost:8080/sendTo:

Send message to user:
```
{
    "status":"CHAT",
    "message": "yourmessage",
    "senderName":"yourusername",
    "receiverName":"receiverusername"
}
```

Note: you can also send messages with browser in: http://localhost:8080

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Apache Kafka](https://kafka.apache.org/)
- [Docker](https://www.docker.com/#build)