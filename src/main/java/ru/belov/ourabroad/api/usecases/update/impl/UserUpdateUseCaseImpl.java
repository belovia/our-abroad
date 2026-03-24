package ru.belov.ourabroad.api.usecases.update.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.api.usecases.update.UserUpdateUsecase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class UserUpdateUseCaseImpl extends AbstractUserUseCase implements UserUpdateUsecase {

    private final UserValidator userValidator;

    public UserUpdateUseCaseImpl(
            UserService userService,
            UserValidator userValidator
    ) {
        super(userService);
        this.userValidator = userValidator;
    }

    @Override
    public void updateUser(String userId, UpdateUserRequest request) {

        Context context = new Context();

        validateInputFields(request, context);

        User user = findExistsUser(userId, context);

        prepareUserToUpdate(request, user, userId);

        persistUser(user, userId, context);
    }

    private void persistUser(User user, String userId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        userService.update(user, context);
    }

    private void validateInputFields(UpdateUserRequest request, Context context) {
        if (!context.isSuccess()) {
            return;
        }

        userValidator.validateEmail(request.getEmail(), context);
        userValidator.validatePhone(request.getPhone(), context);
        userValidator.validateTelegram(request.getTelegramUsername(), context);
        userValidator.validateWhatsapp(request.getWhatsappNumber(), context);
    }

    private void prepareUserToUpdate(UpdateUserRequest request, User user, String userId) {
        log.info("[userId: {}] Preparing user to update", userId);
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
    }
}
