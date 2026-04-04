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
    public ResponseEntity<GetUserByPhoneUseCase.Response> getByPhone(@RequestParam String phone) {
        log.info("GET /api/users/by-phone");
        var response = getUserByPhoneUseCase.execute(new GetUserByPhoneUseCase.Request(phone));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> patchUser(@RequestBody UpdateUserRequest request) {
        log.info("PATCH /api/users/me");
        userUpdateUsecase.updateUser(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/me/email")
    public ResponseEntity<ChangeUserEmailUseCase.Response> changeEmail(@RequestBody ChangeEmailBody body) {
        log.info("PUT /api/users/me/email");
        var response = changeUserEmailUseCase.execute(
                new ChangeUserEmailUseCase.Request(body.newEmail())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<ChangeUserPasswordUseCase.Response> changePassword(@RequestBody ChangePasswordBody body) {
        log.info("PUT /api/users/me/password");
        var response = changeUserPasswordUseCase.execute(
                new ChangeUserPasswordUseCase.Request(body.oldPassword(), body.newPassword())
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/phone")
    public ResponseEntity<ChangeUserPhoneUseCase.Response> changePhone(@RequestBody ChangePhoneBody body) {
        log.info("PUT /api/users/me/phone");
        var response = changeUserPhoneUseCase.execute(
                new ChangeUserPhoneUseCase.Request(body.newPhone())
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
