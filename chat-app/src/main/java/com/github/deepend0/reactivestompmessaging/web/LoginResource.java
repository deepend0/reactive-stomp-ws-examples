package com.github.deepend0.reactivestompmessaging.web;


import com.github.deepend0.reactivestompmessaging.model.UserDto;
import com.github.deepend0.reactivestompmessaging.service.UserService;
import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    private final UserService userService;

    public LoginResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("/login")
    public RestResponse<String> login(UserDto userDto) {
        try {
            userService.addUser(userDto.user());
            return RestResponse.ResponseBuilder.<String>ok().build();
        } catch (IllegalArgumentException e) {
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    public RestResponse<String> logout(UserDto userDto) {
        try {
            userService.removeUser(userDto.user());
            return RestResponse.ResponseBuilder.<String>ok().build();
        } catch (IllegalArgumentException e) {
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, e.getMessage()).build();
        }
    }
}
