package com.github.deepend0.reactivestompmessaging.web;

import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpoint;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.MessageEndpointResponse;
import com.github.deepend0.reactivestomp.messaging.messageendpoint.PathParam;
import com.github.deepend0.reactivestompmessaging.model.*;
import com.github.deepend0.reactivestompmessaging.service.ChatService;
import com.github.deepend0.reactivestompmessaging.service.UserService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class ChatMessageEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageEndpoint.class);

    private final UserService userService;
    private final ChatService chatService;

    public ChatMessageEndpoint(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/connect/{user}")
    public Multi<MessageEndpointResponse<?>> connect(@PathParam String user, UserDto userDto) {
        Set<String> currentUsers = userService.getCurrentUsers();
        return Multi.createFrom().items(
                MessageEndpointResponse.of("/topic/user/" + user + "/currentUsers", new UsersDto(currentUsers)),
                MessageEndpointResponse.of("/topic/users/connected", userDto));
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/create")
    public Multi<MessageEndpointResponse<?>> startChatWith(ChannelRequest channelRequest) {
        String channelId = chatService.createChannel(Set.of(channelRequest.users()));
        ChannelDto channelDto = new ChannelDto(channelId, channelRequest.users());
        return Multi.createFrom().items(MessageEndpointResponse.of("/topic/user/" + channelRequest.users()[0] + "/channel", channelDto),
                MessageEndpointResponse.of("/topic/user/" + channelRequest.users()[1] + "/channel", channelDto));
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/{channelId}/message", outboundDestination = "/topic/channel/{channelId}")
    public Uni<MessageDto> sendMessage(MessageDto messageDto) {
        if(userService.getCurrentUsers().contains(messageDto.sender())) {
            if(messageDto.message().length() > 250) {
                return Uni.createFrom().failure(new IllegalArgumentException("Message too long"));
            }
            return Uni.createFrom().item(messageDto);
        }
        return Uni.createFrom().failure(new IllegalArgumentException("User doesn't exist"));
    }

    @MessageEndpoint(inboundDestination = "/endpoint/chat/disconnect/{user}", outboundDestination = "/topic/users/disconnected")
    public Uni<UserDto> disconnect(@PathParam String user, UserDto userDto) {
        userService.removeUser(user);
        return Uni.createFrom().item(userDto);
    }
}
