package ru.belov.ourabroad.api.usecases.create.review.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.review.CreateSpecialistReviewUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.poi.storage.SpecialistReviewRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CreateSpecialistReviewUseCaseImpl.class)
class CreateSpecialistReviewUseCaseImplTest {

    private static final String USER_ID = "client-1";
    private static final String BOOKING_ID = "booking-1";
    private static final String PROFILE_ID = "sp-1";

    @MockitoBean
    private CurrentUserProvider currentUserProvider;
    @MockitoBean
    private BookingService bookingService;
    @MockitoBean
    private SpecialistProfileService specialistProfileService;
    @MockitoBean
    private SpecialistReviewRepository reviewRepository;

    @Autowired
    private CreateSpecialistReviewUseCase useCase;

    @Test
    void WHEN_confirmedPastVisitWithText_THEN_savedAndStatsApplied() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking(USER_ID, BookingStatus.CONFIRMED, LocalDateTime.now().minusDays(1)));
        when(reviewRepository.existsByBookingId(BOOKING_ID)).thenReturn(false);
        when(reviewRepository.averageScoreAndCount(PROFILE_ID))
                .thenReturn(new SpecialistReviewRepository.ReviewStats(4.0, 1));
        SpecialistProfile profile = profile(0.0, 0);
        when(specialistProfileService.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, "хорошо"));

        assertThat(response.success()).isTrue();
        assertThat(response.reviewId()).isNotBlank();
        assertThat(profile.getRating()).isEqualTo(4.0);
        assertThat(profile.getReviewsCount()).isEqualTo(1);
        verify(reviewRepository).save(any(SpecialistReview.class));
        verify(specialistProfileService).update(eq(profile), any(Context.class));
    }

    @Test
    void WHEN_confirmedPastVisitWithoutText_THEN_saved() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking(USER_ID, BookingStatus.CONFIRMED, LocalDateTime.now().minusHours(2)));
        when(reviewRepository.existsByBookingId(BOOKING_ID)).thenReturn(false);
        when(reviewRepository.averageScoreAndCount(PROFILE_ID))
                .thenReturn(new SpecialistReviewRepository.ReviewStats(4.0, 1));
        when(specialistProfileService.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile(0.0, 0));

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, null));

        assertThat(response.success()).isTrue();
        verify(reviewRepository).save(any(SpecialistReview.class));
    }

    @Test
    void WHEN_secondReviewForProfile_THEN_averageApplied() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking(USER_ID, BookingStatus.CONFIRMED, LocalDateTime.now().minusDays(1)));
        when(reviewRepository.existsByBookingId(BOOKING_ID)).thenReturn(false);
        when(reviewRepository.averageScoreAndCount(PROFILE_ID))
                .thenReturn(new SpecialistReviewRepository.ReviewStats(4.5, 2));
        SpecialistProfile profile = profile(4.0, 1);
        when(specialistProfileService.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 5, "отлично"));

        assertThat(response.success()).isTrue();
        assertThat(profile.getRating()).isEqualTo(4.5);
        assertThat(profile.getReviewsCount()).isEqualTo(2);
    }

    @Test
    void WHEN_pendingBooking_THEN_reviewNotAllowed() {
        notAllowed(BookingStatus.PENDING, LocalDateTime.now().minusDays(1));
    }

    @Test
    void WHEN_cancelledBooking_THEN_reviewNotAllowed() {
        notAllowed(BookingStatus.CANCELLED, LocalDateTime.now().minusDays(1));
    }

    @Test
    void WHEN_visitInFuture_THEN_reviewNotAllowed() {
        notAllowed(BookingStatus.CONFIRMED, LocalDateTime.now().plusDays(1));
    }

    @Test
    void WHEN_foreignBooking_THEN_accessDenied() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking("someone-else", BookingStatus.CONFIRMED, LocalDateTime.now().minusDays(1)));

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.ACCESS_DENIED.getMessage());
        verify(reviewRepository, never()).save(any(SpecialistReview.class));
        verify(specialistProfileService, never()).update(any(), any());
    }

    @Test
    void WHEN_unknownBooking_THEN_bookingNotFound() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class))).thenAnswer(invocation -> {
            Context context = invocation.getArgument(1);
            context.setError(ErrorCode.BOOKING_NOT_FOUND);
            return null;
        });

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.BOOKING_NOT_FOUND.getMessage());
        verify(specialistProfileService, never()).update(any(), any());
    }

    @Test
    void WHEN_reviewAlreadyExists_THEN_alreadyExistsAndProfileUntouched() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking(USER_ID, BookingStatus.CONFIRMED, LocalDateTime.now().minusDays(1)));
        when(reviewRepository.existsByBookingId(BOOKING_ID)).thenReturn(true);

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.REVIEW_ALREADY_EXISTS.getMessage());
        verify(reviewRepository, never()).save(any(SpecialistReview.class));
        verify(specialistProfileService, never()).update(any(), any());
    }

    @Test
    void WHEN_scoreZero_THEN_scoreInvalid() {
        scoreInvalid(0);
    }

    @Test
    void WHEN_scoreSix_THEN_scoreInvalid() {
        scoreInvalid(6);
    }

    @Test
    void WHEN_blankBookingId_THEN_requestValidationError() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(" ", 4, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR.getMessage());
        verify(bookingService, never()).findById(anyString(), any(Context.class));
        verify(specialistProfileService, never()).update(any(), any());
    }

    private void notAllowed(BookingStatus status, LocalDateTime startTime) {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(bookingService.findById(eq(BOOKING_ID), any(Context.class)))
                .thenReturn(booking(USER_ID, status, startTime));

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, 4, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.REVIEW_NOT_ALLOWED.getMessage());
        verify(reviewRepository, never()).save(any(SpecialistReview.class));
        verify(specialistProfileService, never()).update(any(), any());
    }

    private void scoreInvalid(int score) {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);

        var response = useCase.execute(new CreateSpecialistReviewUseCase.Request(BOOKING_ID, score, null));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.REVIEW_SCORE_INVALID.getMessage());
        verify(reviewRepository, never()).save(any(SpecialistReview.class));
        verify(specialistProfileService, never()).update(any(), any());
    }

    private static Booking booking(String userId, BookingStatus status, LocalDateTime startTime) {
        return Booking.builder()
                .id(BOOKING_ID)
                .userId(userId)
                .specialistId(PROFILE_ID)
                .serviceId("srv-1")
                .startTime(startTime)
                .status(status)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
    }

    private static SpecialistProfile profile(double rating, int reviewsCount) {
        return SpecialistProfile.builder()
                .id(PROFILE_ID)
                .userId("spec-user-1")
                .rating(rating)
                .reviewsCount(reviewsCount)
                .build();
    }
}
