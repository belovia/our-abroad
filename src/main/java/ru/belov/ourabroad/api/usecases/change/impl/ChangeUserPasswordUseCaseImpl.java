package ru.belov.ourabroad.api.usecases.change.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.validators.IdValidator;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

import static ru.belov.ourabroad.web.validators.ValidationError.PASSWORDS_ARE_NOT_EQUAL;

@Service
@Slf4j
@Transactional
public class ChangeUserPasswordUseCaseImpl extends AbstractUserUseCase implements ChangeUserPasswordUseCase {

    private final UserValidator userValidator;

    public ChangeUserPasswordUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }
    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {

        IdValidator.requireUserId(userId);

        ValidationResult result = userValidator.validatePassword(newPassword);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = getUserOrThrow(userId);

        boolean passwordsAreEqual = validateOldPassword(user.getPassword(), oldPassword);
        if (passwordsAreEqual) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
        log.info("Passwords are not equal");
        ValidationResult passwordValidation = ValidationResult.withError(PASSWORDS_ARE_NOT_EQUAL);
        throw new ValidationException(passwordValidation);
    }

    protected boolean validateOldPassword(String passwordFromDb, String passwordFromRequest) {
        return passwordFromDb.equals(passwordFromRequest);
    }
}
