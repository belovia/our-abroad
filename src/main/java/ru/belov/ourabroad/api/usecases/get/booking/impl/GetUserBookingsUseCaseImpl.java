package ru.belov.ourabroad.api.usecases.get.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.get.booking.GetUserBookingsUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserBookingsUseCaseImpl implements GetUserBookingsUseCase {

    private final BookingService bookingService;
    private final FieldValidator fieldValidator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        log.info("[userId: {}] Get user bookings", request.userId());

        fieldValidator.validateRequiredField(request.userId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context.getErrorMessage());
        }

        List<Booking> bookings = bookingService.getUserBookings(request.userId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context.getErrorMessage());
        }

        List<BookingItem> items = bookings.stream().map(this::toItem).toList();
        return new Response(true, ErrorCode.SUCCESS.getMessage(), items);
    }

    private BookingItem toItem(Booking b) {
        return new BookingItem(
                b.getId(),
                b.getUserId(),
                b.getSpecialistId(),
                b.getServiceId(),
                b.getStartTime(),
                b.getStatus(),
                b.getCreatedAt()
        );
    }

    private static Response errorResponse(String message) {
        String safeMessage = StringUtils.hasText(message) ? message : ErrorCode.REQUEST_VALIDATION_ERROR.getMessage();
        return new Response(false, safeMessage, List.of());
    }
}
