package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByIdUsecase;
import ru.belov.ourabroad.api.usecases.update.UserUpdateUsecase;
import ru.belov.ourabroad.core.domain.User;

import static ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Request;
import static ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Response;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UserUpdateUsecase userUpdateUsecase;
    private final GetUserByIdUsecase getUserByIdUsecase;
    private final GetUserByEmailUseCase getUserByEmailIdUseCase;

    @PostMapping
    public ResponseEntity<Response> create(
            @RequestBody Request request
    ) {
        Response response = createUserUseCase.execute(request);

        return ResponseEntity.ok(response);
    }

    // ======= GET ========
    @GetMapping("/id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = getUserByIdUsecase.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = getUserByEmailIdUseCase.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


//    // ======= UPDATE ========
//    @PatchMapping("/{userId}/email")
//    public ResponseEntity<Void> updateEmail(
//            @RequestBody UpdateEmailRequest request
//    ) {
//        userUpdateUsecase.updateEmail(request.getUserId(), request.getEmail());
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{userId}/password")
//    public ResponseEntity<Void> updatePassword(
//            @RequestBody UpdatePasswordRequest request
//    ) {
//        userUpdateUsecase.updatePassword(request.getUserId(), request.getPassword());
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{userId}/phone")
//    public ResponseEntity<Void> updateEmail(
//            @RequestBody UpdatePhoneRequest request
//    ) {
//        userUpdateUsecase.updatePhone(request.getUserId(), request.getPhone());
//        return ResponseEntity.noContent().build();
//    }
}
