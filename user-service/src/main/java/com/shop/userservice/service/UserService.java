package com.shop.userservice.service;

import com.shop.userservice.dto.CreateUserRequest;
import com.shop.userservice.dto.UserResponse;
import com.shop.userservice.entity.User;
import com.shop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже занят: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username уже занят: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // демо вариант, на проектах разный способ кеширования и шифрования
                .build();

        User saved = userRepository.save(user);
        log.info("Создан новый пользователь: {}", saved.getUsername());

        return toResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
