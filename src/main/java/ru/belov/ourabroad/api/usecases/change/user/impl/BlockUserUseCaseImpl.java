package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.BlockUserUseCase;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class BlockUserUseCaseImpl extends AbstractUserUseCase implements BlockUserUseCase {

    private final UserValidator userValidator;
    private final RefreshTokenService refreshTokenService;

    public BlockUserUseCaseImpl(
            UserService userService,
            UserValidator userValidator,
            RefreshTokenService refreshTokenService
    ) {
        super(userService);
        this.userValidator = userValidator;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Block user", userId);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        User user = findExistsUser(userId, context);
        if (!context.isSuccess() || user == null) {
            return errorResponse(userId, context);
        }

        user.block();
        userService.update(user, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        refreshTokenService.revokeAllForUser(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        log.info("[userId: {}] User blocked", userId);
        return new Response(userId, true, ErrorCode.SUCCESS.getMessage());
    }

    private Response errorResponse(String userId, Context context) {
        log.error("[userId: {}] Block failed: {}", userId, context.getErrorMessage());
        return new Response(userId, false, context.getErrorMessage());
    }
}
