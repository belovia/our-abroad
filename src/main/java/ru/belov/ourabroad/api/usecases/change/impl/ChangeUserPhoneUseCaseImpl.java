package ru.belov.ourabroad.api.usecases.change.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.ChangeUserPhoneUseCase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.validators.IdValidator;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

@Service
@Slf4j
@Transactional
public class ChangeUserPhoneUseCaseImpl extends AbstractUserUseCase implements ChangeUserPhoneUseCase {

    private final UserValidator userValidator;

    public ChangeUserPhoneUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }

    @Override
    public void changePhone(String userId, String newPhone) {

        IdValidator.requireUserId(userId);

        ValidationResult result = userValidator.validatePhone(newPhone);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = getUserOrThrow(userId);

        user.setPhone(newPhone);

        userRepository.save(user);
    }
}
