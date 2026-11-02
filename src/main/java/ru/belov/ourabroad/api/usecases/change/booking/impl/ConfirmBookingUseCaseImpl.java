package ru.belov.ourabroad.api.usecases.change.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.change.booking.ConfirmBookingUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmBookingUseCaseImpl implements ConfirmBookingUseCase {

    private final BookingService bookingService;
    private final FieldValidator fieldValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String specialistUserId = currentUserProvider.requiredUserId();
        log.info("[bookingId: {}] Confirm booking by specialist user {}", request.bookingId(), specialistUserId);

        fieldValidator.validateRequiredField(request.bookingId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context.getErrorMessage());
        }

        bookingService.confirmBooking(request.bookingId(), specialistUserId, context);
        if (!context.isSuccess()) {
            return errorResponse(context.getErrorMessage());
        }

        return new Response(true, ErrorCode.SUCCESS.getMessage());
    }

    private static Response errorResponse(String message) {
        String safeMessage = StringUtils.hasText(message) ? message : ErrorCode.REQUEST_VALIDATION_ERROR.getMessage();
        return new Response(false, safeMessage);
    }
}
