package ru.belov.ourabroad.api.usecases.create.review.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.review.CreateSpecialistReviewUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.core.domain.SpecialistReviewFactory;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.poi.storage.SpecialistReviewRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateSpecialistReviewUseCaseImpl implements CreateSpecialistReviewUseCase {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;

    private final CurrentUserProvider currentUserProvider;
    private final BookingService bookingService;
    private final SpecialistProfileService specialistProfileService;
    private final SpecialistReviewRepository reviewRepository;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Create specialist review", userId);

        if (!StringUtils.hasText(request.bookingId())) {
            context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
            return errorResponse(null, context);
        }
        if (request.score() < MIN_SCORE || request.score() > MAX_SCORE) {
            context.setError(ErrorCode.REVIEW_SCORE_INVALID);
            return errorResponse(null, context);
        }

        Booking booking = bookingService.findById(request.bookingId(), context);
        if (!context.isSuccess() || booking == null) {
            return errorResponse(null, context);
        }
        if (!userId.equals(booking.getUserId())) {
            context.setError(ErrorCode.ACCESS_DENIED);
            return errorResponse(null, context);
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED
                || booking.getStartTime() == null
                || booking.getStartTime().isAfter(LocalDateTime.now())) {
            context.setError(ErrorCode.REVIEW_NOT_ALLOWED);
            return errorResponse(null, context);
        }
        if (reviewRepository.existsByBookingId(booking.getId())) {
            context.setError(ErrorCode.REVIEW_ALREADY_EXISTS);
            return errorResponse(null, context);
        }

        SpecialistReview review = SpecialistReviewFactory.create(
                booking.getId(),
                userId,
                booking.getSpecialistId(),
                request.score(),
                StringUtils.hasText(request.text()) ? request.text() : null
        );
        reviewRepository.save(review);

        SpecialistReviewRepository.ReviewStats stats =
                reviewRepository.averageScoreAndCount(booking.getSpecialistId());
        SpecialistProfile profile = specialistProfileService.findById(booking.getSpecialistId(), context);
        if (!context.isSuccess() || profile == null) {
            return errorResponse(null, context);
        }
        profile.applyReviewStats(stats.rating(), stats.reviewsCount());
        specialistProfileService.update(profile, context);
        if (!context.isSuccess()) {
            return errorResponse(null, context);
        }

        log.info("[reviewId: {}] Review saved for specialist {}", review.getId(), booking.getSpecialistId());
        return new Response(review.getId(), true, ErrorCode.SUCCESS.getMessage());
    }

    private Response errorResponse(String reviewId, Context context) {
        log.error("Create review failed: {}", context.getErrorMessage());
        return new Response(reviewId, false, context.getErrorMessage());
    }
}
