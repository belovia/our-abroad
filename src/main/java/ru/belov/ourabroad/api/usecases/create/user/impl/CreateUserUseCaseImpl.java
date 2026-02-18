package ru.belov.ourabroad.api.usecases.create.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.UserAlreadyExistsException;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.dto.create.CreateUserRequest;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public String create(CreateUserRequest request) {

        ValidationResult result = userValidator.validateCreateUserRequest(request);
        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        String userId = UUID.randomUUID().toString();
        log.info("[userId: {}] Creating user with id: {}",
                userId,
                userId);

        User user = UserFactory.newUser(
                userId,
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getTelegramUsername(),
                request.getWhatsAppNumber(),
                request.getActivity()
        );

        boolean userEmailExists = userRepository.existsByEmail(request.getEmail());

        if (userEmailExists) {
            log.info("[userId: {}] User already exists with email: {}",
                    userId,
                    request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        userRepository.save(user);

        return userId;
    }
}
