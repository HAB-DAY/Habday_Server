package com.habday.server.controller;

import com.habday.server.entity.User;
import com.habday.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/user")
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
