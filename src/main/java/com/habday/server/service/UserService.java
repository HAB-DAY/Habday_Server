package com.habday.server.service;

import com.habday.server.entity.User;
import com.habday.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findOne(Long id) {
        return userRepository.findById(id);
    }
}
