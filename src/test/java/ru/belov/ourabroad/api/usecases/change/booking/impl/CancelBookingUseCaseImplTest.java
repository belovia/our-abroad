package ru.belov.ourabroad.api.usecases.change.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.booking.CancelBookingUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CancelBookingUseCaseImpl.class,
        FieldValidator.class
})
class CancelBookingUseCaseImplTest {

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private CancelBookingUseCaseImpl useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_cancelValid_THEN_success() {
        when(currentUserProvider.requiredUserId()).thenReturn("u1");
        doNothing().when(bookingService).cancelBooking(eq("b1"), eq("u1"), any(Context.class));

        CancelBookingUseCase.Response response = useCase.execute(new CancelBookingUseCase.Request("b1"));

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());
    }

    @Test
    void WHEN_alreadyCancelled_THEN_invalidState() {
        when(currentUserProvider.requiredUserId()).thenReturn("u1");
        doAnswer(invocation -> {
            Context ctx = invocation.getArgument(2);
            ctx.setError(ErrorCode.INVALID_BOOKING_STATE);
            return null;
        }).when(bookingService).cancelBooking(eq("b1"), eq("u1"), any(Context.class));

        CancelBookingUseCase.Response response = useCase.execute(new CancelBookingUseCase.Request("b1"));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.INVALID_BOOKING_STATE.getMessage());
    }
}
