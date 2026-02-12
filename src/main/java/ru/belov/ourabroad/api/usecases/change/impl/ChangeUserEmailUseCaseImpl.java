package ru.belov.ourabroad.api.usecases.change.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.ChangeUserEmailUseCase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.UserAlreadyExistsException;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.validators.IdValidator;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

@Service
@Slf4j
@Transactional
public class ChangeUserEmailUseCaseImpl extends AbstractUserUseCase implements ChangeUserEmailUseCase {

    private final UserValidator userValidator;

    public ChangeUserEmailUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }

    @Override
    public void changeEmail(String userId, String newEmail) {

        IdValidator.requireUserId(userId);

        ValidationResult result = userValidator.validateEmail(newEmail);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = getUserOrThrow(userId);

        boolean emailExists = userRepository.existsByEmail(newEmail);

        if (emailExists) {
            throw new UserAlreadyExistsException("email");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }
}
