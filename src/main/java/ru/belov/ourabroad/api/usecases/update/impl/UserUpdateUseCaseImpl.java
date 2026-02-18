package ru.belov.ourabroad.api.usecases.update.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.update.UserUpdateUsecase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.UserAlreadyExistsException;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;
import ru.belov.ourabroad.web.validators.IdValidator;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

@Service
@Slf4j
@Transactional
public class UserUpdateUseCaseImpl extends AbstractUserUseCase implements UserUpdateUsecase {

    private final UserValidator userValidator;

    public UserUpdateUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }

    @Override
    public void updateUser(String userId, UpdateUserRequest request) {

        IdValidator.requireUserId(userId);

        ValidationResult result = ValidationResult.ok();

        if (request.getEmail() != null) {
            result.merge(userValidator.validateEmail(request.getEmail()));
        }

        if (request.getPhone() != null) {
            result.merge(userValidator.validatePhone(request.getPhone()));
        }

        if (request.getTelegramUsername() != null) {
            result.merge(userValidator.validateTelegram(request.getTelegramUsername()));
        }

        if (request.getWhatsappNumber() != null) {
            result.merge(userValidator.validateWhatsapp(request.getWhatsappNumber()));
        }

        result.throwIfInvalid();

        User user = getUserOrThrow(userId);

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getTelegramUsername() != null) {
            user.setTelegramUsername(request.getTelegramUsername());
        }
        if (request.getWhatsappNumber() != null) {
            user.setWhatsappNumber(request.getWhatsappNumber());
        }
        if (request.getActivity() != null) {
            user.setActivity(request.getActivity());
        }

        userRepository.save(user);
    }
}
