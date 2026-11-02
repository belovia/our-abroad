package ru.belov.ourabroad.api.usecases.change.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.booking.ConfirmBookingUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ConfirmBookingUseCaseImpl.class,
        FieldValidator.class
})
class ConfirmBookingUseCaseImplTest {

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private ConfirmBookingUseCaseImpl useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_confirmValid_THEN_success() {
        when(currentUserProvider.requiredUserId()).thenReturn("spec-user");
        doNothing().when(bookingService).confirmBooking(eq("b1"), eq("spec-user"), any(Context.class));

        ConfirmBookingUseCase.Response response = useCase.execute(
                new ConfirmBookingUseCase.Request("b1")
        );

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());
    }

    @Test
    void WHEN_notOwner_THEN_accessDenied() {
        when(currentUserProvider.requiredUserId()).thenReturn("other-user");
        doAnswer(invocation -> {
            Context ctx = invocation.getArgument(2);
            ctx.setError(ErrorCode.ACCESS_DENIED);
            return null;
        }).when(bookingService).confirmBooking(eq("b1"), eq("other-user"), any(Context.class));

        ConfirmBookingUseCase.Response response = useCase.execute(
                new ConfirmBookingUseCase.Request("b1")
        );

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.ACCESS_DENIED.getMessage());
    }
}
