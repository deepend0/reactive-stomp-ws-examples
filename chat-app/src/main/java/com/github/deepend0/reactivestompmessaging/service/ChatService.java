package com.github.deepend0.reactivestompmessaging.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    private final Map<String, Set<String>> userChats = new ConcurrentHashMap<>();
    private final UserService userService;

    public ChatService(UserService userService) {
        this.userService = userService;
    }

    public String createChannel(Set<String> users) {
        if(userService.getCurrentUsers().containsAll(users)) {
            throw new IllegalArgumentException("All users must be logged in");
        }
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("Users cannot be null or empty");
        }
        if (userChats.containsValue(users)) {
            throw new IllegalArgumentException("Channel with these users already exists");
        }
        String channelId = UUID.randomUUID().toString();
        userChats.put(channelId, users);
        return channelId;
    }
}
