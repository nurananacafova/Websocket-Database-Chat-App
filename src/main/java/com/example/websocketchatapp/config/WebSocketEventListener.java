package com.example.websocketchatapp.config;

import com.example.websocketchatapp.model.Message;
import com.example.websocketchatapp.model.MessageStatus;
import com.example.websocketchatapp.model.Status;
import com.example.websocketchatapp.model.User;
import com.example.websocketchatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        log.info("Received a new web socket connection");
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        if (headerAccessor.getSessionAttributes() != null) {
//            String username = headerAccessor.getUser().getName();
//            User user = createUserIfNotExists(username, headerAccessor.getSessionId());
//            headerAccessor.getSessionAttributes().put("username", username);
//        }
//        var message = Message.builder()
//                .users(userRepository.findAll())
//                .build();
//
//        messagingTemplate.convertAndSend("/chatroom/public", message);
//
//    }
//
//    private Message createMessageWithUserList() {
//        Message message = new Message();
//        message.setUsers(userRepository.findAll());
//        return message;
//    }
//
//    private User createUserIfNotExists(String username, String sessionId) {
//        Optional<User> existedUser = userRepository.findByName(username);
//        if (existedUser.isPresent()) {
//            User user = existedUser.get();
//            user.setSessionId(sessionId);
//            user.setStatus(Status.ONLINE);
//            userRepository.save(user);
//            log.info("Status changed to ONLINE for user: {}.", username);
//            return user;
//        } else {
//            User newUser = new User();
//            newUser.setName(username);
//            newUser.setStatus(Status.ONLINE);
//            newUser.setSessionId(sessionId);
//            userRepository.save(newUser);
//            log.info("New User created.");
//            return newUser;
//        }
//    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Connectent event: {}", event);
        Message message = new Message();
        log.info("Message: {}",message);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("getSessionAttributes: {}", headerAccessor.getSessionAttributes());
        if (headerAccessor.getSessionAttributes() != null) {
            String username = headerAccessor.getUser().getName();
            log.info("USERNAME: {}", username);
            createUser(username, message, headerAccessor);
            headerAccessor.getSessionAttributes().put("username", username);
        }
        log.info("Headeraccessor: {}", headerAccessor);
//        messagingTemplate.convertAndSend("/chatroom/public", message);
    }

    private User createUser(String username, Message message, StompHeaderAccessor headerAccessor) {
        Optional<User> existedUser = userRepository.findByName(username);
        if (existedUser.isPresent()) {
            User user=existedUser.get();
            user.setSessionId(headerAccessor.getSessionId());
            user.setStatus(Status.ONLINE);
            userRepository.save(user);
            log.info("Status changed to ONLINE.");
            return user;
        } else {
            User newUser=new User();
            newUser.setName(username);
            log.info("sender name {}", username);
            newUser.setStatus(Status.ONLINE);
            newUser.setSessionId(headerAccessor.getSessionId());
            userRepository.save(newUser);
            log.info("New User created.");
            return newUser;
        }
    }

    public boolean isUserExists(String name) {
        Optional<User> user = userRepository.findByName(name);
        return user.isPresent();
    }


    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        log.info("headerAccessor: {}", headerAccessor);
        log.info("Session Attributes: {}", headerAccessor.getSessionAttributes());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User disconnected: {}", username);

            var chatMessage = Message.builder()
                    .status(MessageStatus.LEAVE)
                    .senderName(username)
                    .build();
            messagingTemplate.convertAndSend("/chatroom/public", chatMessage);
            changeUserStatus(headerAccessor.getSessionId());
        }
    }

    private void changeUserStatus(String sessionId) {
        User foundedUser = userRepository.findBySessionId(sessionId);
        foundedUser.setStatus(Status.OFFLINE);
        userRepository.save(foundedUser);
        log.info("Status changed to OFFLINE.");
    }


    //    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        log.info("Session ID: {}", sessionId);
//        User user = userRepository.findBySessionId(sessionId);
//        if (user != null) {
//            user.setStatus(Status.OFFLINE);
//            userRepository.save(user);
//            log.info("User disconnected: {}", user.getName());
//            broadcastUserList();
//        }
//    }
}
