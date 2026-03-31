package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserEmailUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPhoneUseCase;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByIdUsecase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByPhoneUseCase;
import ru.belov.ourabroad.api.usecases.update.UserUpdateUsecase;
import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;

import static ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Request;
import static ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Response;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UserUpdateUsecase userUpdateUsecase;
    private final GetUserByIdUsecase getUserByIdUsecase;
    private final GetUserByEmailUseCase getUserByEmailUseCase;
    private final GetUserByPhoneUseCase getUserByPhoneUseCase;
    private final ChangeUserEmailUseCase changeUserEmailUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final ChangeUserPhoneUseCase changeUserPhoneUseCase;

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody Request request) {
        Response response = createUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<GetUserByIdUsecase.Response> getById(@PathVariable("userId") String userId) {
        log.info("[userId: {}] GET /api/users/{}", userId);
        GetUserByIdUsecase.Response response = getUserByIdUsecase.execute(new GetUserByIdUsecase.Request(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<GetUserByEmailUseCase.Response> getByEmail(
            @PathVariable("email") String email
    ) {
        var response = getUserByEmailUseCase.execute(new GetUserByEmailUseCase.Request(email));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-phone")
    public ResponseEntity<GetUserByPhoneUseCase.Response> getByPhone(
            @RequestParam String userId,
            @RequestParam String phone
    ) {
        log.info("[userId: {}] GET /api/users/by-phone", userId);
        var response = getUserByPhoneUseCase.execute(new GetUserByPhoneUseCase.Request(userId, phone));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> patchUser(
            @PathVariable("userId") String userId,
            @RequestBody UpdateUserRequest request
    ) {
        log.info("[userId: {}] PATCH /api/users/{}", userId);
        userUpdateUsecase.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<ChangeUserEmailUseCase.Response> changeEmail(
            @PathVariable("userId") String userId,
            @RequestBody ChangeEmailBody body
    ) {
        log.info("[userId: {}] PUT .../email", userId);
        var response = changeUserEmailUseCase.execute(
                new ChangeUserEmailUseCase.Request(userId, body.newEmail())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ChangeUserPasswordUseCase.Response> changePassword(
            @PathVariable("userId") String userId,
            @RequestBody ChangePasswordBody body
    ) {
        log.info("[userId: {}] PUT .../password", userId);
        var response = changeUserPasswordUseCase.execute(
                new ChangeUserPasswordUseCase.Request(userId, body.oldPassword(), body.newPassword())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/phone")
    public ResponseEntity<ChangeUserPhoneUseCase.Response> changePhone(
            @PathVariable("userId") String userId,
            @RequestBody ChangePhoneBody body
    ) {
        log.info("[userId: {}] PUT .../phone", userId);
        var response = changeUserPhoneUseCase.execute(
                new ChangeUserPhoneUseCase.Request(userId, body.newPhone())
        );
        return ResponseEntity.ok(response);
    }

    public record ChangeEmailBody(String newEmail) {
    }

    public record ChangePasswordBody(String oldPassword, String newPassword) {
    }

    public record ChangePhoneBody(String newPhone) {
    }
}
