package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.belov.ourabroad.api.usecases.UserUpdateUsecase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.api.usecases.CreateUserUsecase;
import ru.belov.ourabroad.api.usecases.GetUserProfileUseCase;
import ru.belov.ourabroad.api.usecases.GetUserUsecase;
import ru.belov.ourabroad.web.dto.create.CreateUserRequest;
import ru.belov.ourabroad.web.dto.update.UpdateEmailRequest;
import ru.belov.ourabroad.web.dto.update.UpdatePasswordRequest;
import ru.belov.ourabroad.web.dto.update.UpdatePhoneRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUsecase createUserUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserUpdateUsecase userUpdateUsecase;
    private final GetUserUsecase getUserUsecase;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            @RequestBody CreateUserRequest request
    ) {
        String userId = createUserUseCase.create(request);

        return ResponseEntity.ok(Map.of("userId", userId));
    }

    // ======= GET ========
    @GetMapping("/id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = getUserUsecase.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = getUserUsecase.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


    // ======= UPDATE ========
    @PatchMapping("/{userId}/email")
    public ResponseEntity<Void> updateEmail(
            @RequestBody UpdateEmailRequest request
    ) {
        userUpdateUsecase.updateEmail(request.getUserId(), request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody UpdatePasswordRequest request
    ) {
        userUpdateUsecase.updatePassword(request.getUserId(), request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/phone")
    public ResponseEntity<Void> updateEmail(
            @RequestBody UpdatePhoneRequest request
    ) {
        userUpdateUsecase.updatePhone(request.getUserId(), request.getPhone());
        return ResponseEntity.noContent().build();
    }
}
