package com.github.deepend0.reactivestompmessaging.web;

import com.github.deepend0.reactivestomp.messaging.client.MessagingClient;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpoint;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpointResponse;
import com.github.deepend0.reactivestompmessaging.service.ChatService;
import com.github.deepend0.reactivestompmessaging.service.UserService;
import io.smallrye.mutiny.Uni;

import java.util.Set;

public class ChatMessageEndpoint {
    private final UserService userService;
    private final ChatService chatService;
    private final MessagingClient messagingClient;

    public ChatMessageEndpoint(UserService userService, ChatService chatService, MessagingClient messagingClient) {
        this.userService = userService;
        this.chatService = chatService;
        this.messagingClient = messagingClient;
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/connect")
    public Uni<MessageEndpointResponse<Set<String>>> connect(String username) {
        Set<String> currentUsers = userService.getCurrentUsers();
        return Uni.createFrom().item(MessageEndpointResponse.of("/topic/user/" + username + "/connected", currentUsers))
                .call(()->messagingClient.send("/topic/users/connected", username));
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/create")
    public Uni<Void> startChatWith(String username, String targetUsername) {
        String channelId = chatService.createChannel(Set.of(username, targetUsername));
        Uni<Void> user1Uni =  messagingClient.send("/topic/user/" + username + "/channel", channelId);
        Uni<Void> user2Uni =  messagingClient.send("/topic/user/" + targetUsername + "/channel", channelId);
        return Uni.combine().all().unis(user1Uni, user2Uni).discardItems();
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/disconnect")
    public MessageEndpointResponse<String> disconnect(String username) {
        userService.removeUser(username);
        return MessageEndpointResponse.of("/topic/users/disconnected", username);
    }
}
