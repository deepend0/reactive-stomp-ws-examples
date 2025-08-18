package com.github.deepend0.reactivestompmessaging.web;


import com.github.deepend0.reactivestompmessaging.service.UserService;
import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/login")
public class LoginResource {
    private final UserService userService;

    public LoginResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    public RestResponse<String> login(String username) {
        try {
            userService.addUser(username);
            return RestResponse.ResponseBuilder.<String>ok().build();
        } catch (IllegalArgumentException e) {
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, e.getMessage()).build();
        }
    }

    @POST
    public RestResponse<String> logout(String username) {
        try {
            userService.removeUser(username);
            return RestResponse.ResponseBuilder.<String>ok().build();
        } catch (IllegalArgumentException e) {
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, e.getMessage()).build();
        }
    }
}
