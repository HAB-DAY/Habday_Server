package com.habday.server.controller;

import com.habday.server.entity.User;
import com.habday.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/user")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public Optional<User> getUserId(@PathVariable Long userId) {
        return userRepository.findById(userId);
    }
}
