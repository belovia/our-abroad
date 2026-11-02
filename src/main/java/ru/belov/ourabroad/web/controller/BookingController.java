package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.belov.ourabroad.api.usecases.change.booking.CancelBookingUseCase;
import ru.belov.ourabroad.api.usecases.change.booking.ConfirmBookingUseCase;
import ru.belov.ourabroad.api.usecases.create.booking.CreateBookingUseCase;
import ru.belov.ourabroad.api.usecases.get.booking.GetSpecialistBookingsUseCase;
import ru.belov.ourabroad.api.usecases.get.booking.GetUserBookingsUseCase;
import ru.belov.ourabroad.web.dto.BookingResponse;
import ru.belov.ourabroad.web.dto.CreateBookingRequest;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final GetUserBookingsUseCase getUserBookingsUseCase;
    private final GetSpecialistBookingsUseCase getSpecialistBookingsUseCase;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody CreateBookingRequest body) {
        log.info("POST /api/bookings");
        var request = new CreateBookingUseCase.Request(
                body.specialistId(),
                body.serviceId(),
                body.startTime()
        );
        var response = createBookingUseCase.execute(request);
        return ResponseEntity.ok(toBookingResponse(response));
    }

    @GetMapping("/my")
    public ResponseEntity<BookingsListResponse> myBookings() {
        log.info("GET /api/bookings/my");
        var response = getUserBookingsUseCase.execute(new GetUserBookingsUseCase.Request());
        return ResponseEntity.ok(new BookingsListResponse(
                response.success(),
                response.message(),
                response.bookings().stream().map(BookingController::toBookingResponse).toList()
        ));
    }

    @GetMapping("/incoming")
    public ResponseEntity<BookingsListResponse> incoming() {
        log.info("GET /api/bookings/incoming");
        var response = getSpecialistBookingsUseCase.execute(
                new GetSpecialistBookingsUseCase.Request()
        );
        return ResponseEntity.ok(new BookingsListResponse(
                response.success(),
                response.message(),
                response.bookings().stream().map(BookingController::toBookingResponse).toList()
        ));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<CancelBookingUseCase.Response> cancel(
            @PathVariable("id") String bookingId
    ) {
        log.info("[bookingId: {}] PATCH cancel", bookingId);
        var response = cancelBookingUseCase.execute(new CancelBookingUseCase.Request(bookingId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ConfirmBookingUseCase.Response> confirm(
            @PathVariable("id") String bookingId
    ) {
        log.info("[bookingId: {}] PATCH confirm", bookingId);
        var response = confirmBookingUseCase.execute(
                new ConfirmBookingUseCase.Request(bookingId)
        );
        return ResponseEntity.ok(response);
    }

    private static BookingResponse toBookingResponse(CreateBookingUseCase.Response r) {
        return new BookingResponse(
                r.success(),
                r.message(),
                r.bookingId(),
                r.userId(),
                r.specialistId(),
                r.serviceId(),
                r.startTime(),
                r.status(),
                r.createdAt()
        );
    }

    private static BookingResponse toBookingResponse(GetUserBookingsUseCase.BookingItem item) {
        return new BookingResponse(
                null,
                null,
                item.id(),
                item.userId(),
                item.specialistId(),
                item.serviceId(),
                item.startTime(),
                item.status(),
                item.createdAt()
        );
    }

    private static BookingResponse toBookingResponse(GetSpecialistBookingsUseCase.BookingItem item) {
        return new BookingResponse(
                null,
                null,
                item.id(),
                item.userId(),
                item.specialistId(),
                item.serviceId(),
                item.startTime(),
                item.status(),
                item.createdAt()
        );
    }

    public record BookingsListResponse(
            boolean success,
            String message,
            List<BookingResponse> bookings
    ) {
    }
}
