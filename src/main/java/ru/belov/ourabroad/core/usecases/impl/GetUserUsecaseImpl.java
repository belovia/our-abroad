package ru.belov.ourabroad.core.usecases.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.usecases.GetUserUsecase;
import ru.belov.ourabroad.poi.storage.ProfileRepository;
import ru.belov.ourabroad.poi.storage.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUsecaseImpl implements GetUserUsecase {

    private final UserRepository userRepository;

    @Override
    public User getUserById(String userId) {
        if (userId == null || StringUtils.isBlank(userId)) {
            // todo: переделать на более логичный вариант + log
            return null;
        }
        Optional<User> profileOpt = userRepository.findById(userId);

        return profileOpt.orElse(null);
    }
}
