package ru.belov.ourabroad.core.usecases.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.usecases.CreateUserUsecase;
import ru.belov.ourabroad.poi.storage.ProfileRepository;
import ru.belov.ourabroad.poi.storage.ReputationRepository;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.dto.CreateUserRequest;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCaseImpl implements CreateUserUsecase {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ReputationRepository reputationRepository;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public String create(CreateUserRequest command) {

        String userId = UUID.randomUUID().toString();
        log.info("[userId: {}] Creating user with id: {}", userId, userId);

        User user = UserFactory.newUser(
                userId,
                command.getEmail(),
                command.getPhone(),
                command.getPassword());

        userRepository.save(user);

        return userId;
    }
}
