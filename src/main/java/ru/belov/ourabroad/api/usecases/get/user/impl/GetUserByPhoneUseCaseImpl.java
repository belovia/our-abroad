package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByPhoneUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByPhoneUseCaseImpl implements GetUserByPhoneUseCase {

    private final UserService userService;

    @Override
    public User getByUserId(String userId, String userPhone) {
        Context context = new Context();
       return userService.findByPhone(userId, userPhone, context);

    }

}
