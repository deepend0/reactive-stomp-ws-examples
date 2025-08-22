package com.github.deepend0.reactivestompmessaging.service;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class UserService {
    private final ConcurrentHashSet<String> currentUsers = new ConcurrentHashSet<>();

    public void addUser(String username) {
        if (!currentUsers.contains(username)) {
            currentUsers.add(username);
        } else {
            throw new IllegalArgumentException("User already exists");
        }
    }

    public void removeUser(String username) {
        if (currentUsers.contains(username)) {
            currentUsers.remove(username);
        } else {
            throw new IllegalArgumentException("User doesn't exist");
        }
    }

    public Set<String> getCurrentUsers() {
        return new HashSet<>(currentUsers);
    }
}
