package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.usecases.CreateUserUsecase;
import ru.belov.ourabroad.core.usecases.GetUserProfileUseCase;
import ru.belov.ourabroad.core.usecases.GetUserUsecase;
import ru.belov.ourabroad.web.dto.CreateUserRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUsecase createUserUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetUserUsecase getUserUsecase;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            @RequestBody CreateUserRequest request
    ) {
        String userId = createUserUseCase.create(request);

        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = getUserUsecase.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
