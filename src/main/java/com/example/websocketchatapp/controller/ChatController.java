package com.example.websocketchatapp.controller;

import com.example.websocketchatapp.model.Message;
import com.example.websocketchatapp.model.Status;
import com.example.websocketchatapp.model.User;
import com.example.websocketchatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/message")
    public Message receiverMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
//        message.setSessionId(headerAccessor.getSessionId());
        log.info("Sending message to '/chatroom/public': {}", message);
        simpMessagingTemplate.convertAndSend("/chatroom/public", message);
        log.info("Message successfully sent");
        return message;
    }


    @MessageMapping("/private-message")
    public Message privateMessage(@Payload Message message) {
        log.info("Sending message to user: {}", message.getReceiverName());
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        log.info("Message sent to user: {}", message.getReceiverName());
        return message;
    }
//    @MessageMapping("/chat.addUser")
//    @SendTo("/chatroom/public")
//    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
//        if (headerAccessor.getSessionAttributes() != null) {
//            headerAccessor.getSessionAttributes().put("username", message.getSenderName());
//        }
//        return message;
//    }
//    @MessageMapping("/addUser")
//    @SendTo("/chatroom/public")
//    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
//        User user = new User();
//        createUser(user, message, headerAccessor);
//        if (headerAccessor.getSessionAttributes() != null) {
//            headerAccessor.getSessionAttributes().put("username", message.getSenderName());
//        }
//        return message;
//    }
    private User createUser(User user, Message message, SimpMessageHeaderAccessor headerAccessor) {
        if (isUserExists(message.getSenderName())) {
            User foundedUser = userRepository.findByName(message.getSenderName()).get();
            foundedUser.setSessionId(headerAccessor.getSessionId());
            foundedUser.setStatus(Status.ONLINE);
            userRepository.save(foundedUser);
            log.info("Status changed to ONLINE.");
        } else {
            user.setName(message.getSenderName());
            user.setStatus(Status.ONLINE);
            user.setSessionId(headerAccessor.getSessionId());
            userRepository.save(user);
            log.info("New User created.");
        }

        message.setUsers(userRepository.findAll()); // Add this line to include all users
        return user;
    }

    public boolean isUserExists(String name) {
        Optional<User> user = userRepository.findByName(name);
        return user.isPresent();
    }
}


