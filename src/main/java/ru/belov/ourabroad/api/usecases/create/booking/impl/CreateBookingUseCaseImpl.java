package ru.belov.ourabroad.api.usecases.create.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.booking.CreateBookingUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBookingUseCaseImpl implements CreateBookingUseCase {

    private final SpecialistProfileService specialistProfileService;
    private final BookingService bookingService;
    private final FieldValidator fieldValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info(
                "[userId: {}] Create booking for specialist {} service {}",
                userId,
                request.specialistId(),
                request.serviceId()
        );

        fieldValidator.validateRequiredField(request.specialistId(), context);
        fieldValidator.validateRequiredField(request.serviceId(), context);
        if (context.isSuccess() && request.startTime() == null) {
            context.setError(ErrorCode.FIELD_REQUIRED);
        }
        if (!context.isSuccess()) {
            return errorResponse(context.getErrorMessage());
        }

        SpecialistProfile profile = specialistProfileService.findById(request.specialistId(), context);
        if (!context.isSuccess() || profile == null) {
            log.warn("[specialistId: {}] Specialist not found for booking", request.specialistId());
            return errorResponse(ErrorCode.SPECIALIST_NOT_FOUND.getMessage());
        }
        if (!profile.isActive()) {
            log.warn("[specialistId: {}] Specialist inactive", request.specialistId());
            return errorResponse(ErrorCode.SPECIALIST_NOT_FOUND.getMessage());
        }

        Context bookingContext = new Context();
        Booking booking = bookingService.createBooking(
                userId,
                request.specialistId(),
                request.serviceId(),
                request.startTime(),
                bookingContext
        );
        if (!bookingContext.isSuccess() || booking == null) {
            return errorResponse(bookingContext.getErrorMessage());
        }

        return new Response(
                true,
                ErrorCode.SUCCESS.getMessage(),
                booking.getId(),
                booking.getUserId(),
                booking.getSpecialistId(),
                booking.getServiceId(),
                booking.getStartTime(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    private static Response errorResponse(String message) {
        String safeMessage = StringUtils.hasText(message) ? message : ErrorCode.REQUEST_VALIDATION_ERROR.getMessage();
        return new Response(
                false,
                safeMessage,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
