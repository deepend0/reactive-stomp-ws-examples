package com.github.deepend0.reactivestompmessaging.web;

import com.github.deepend0.reactivestomp.messaging.client.MessagingClient;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpoint;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpointResponse;
import com.github.deepend0.reactivestompmessaging.model.ChannelDto;
import com.github.deepend0.reactivestompmessaging.model.ChannelRequest;
import com.github.deepend0.reactivestompmessaging.model.UserDto;
import com.github.deepend0.reactivestompmessaging.service.ChatService;
import com.github.deepend0.reactivestompmessaging.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
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
    public Uni<MessageEndpointResponse<Set<String>>> connect(UserDto userDto) {
        Set<String> currentUsers = userService.getCurrentUsers();
        return Uni.createFrom().item(MessageEndpointResponse.of("/topic/user/" + userDto.user() + "/connected", currentUsers))
                .call(()->messagingClient.send("/topic/users/connected", userDto));
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/create")
    public Uni<Void> startChatWith(ChannelRequest channelRequest) {
        String channelId = chatService.createChannel(Set.of(channelRequest.users()));
        ChannelDto channelDto = new ChannelDto(channelId, channelRequest.users());
        Uni<Void> user1Uni =  messagingClient.send("/topic/user/" + channelRequest.users()[0] + "/channel", channelDto);
        Uni<Void> user2Uni =  messagingClient.send("/topic/user/" + channelRequest.users()[1] + "/channel", channelDto);
        return Uni.combine().all().unis(user1Uni, user2Uni).discardItems();
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/disconnect")
    public MessageEndpointResponse<UserDto> disconnect(UserDto userDto) {
        userService.removeUser(userDto.user());
        return MessageEndpointResponse.of("/topic/users/disconnected", userDto);
    }
}
