package ru.belov.ourabroad.api.usecases.get.review.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.get.review.GetSpecialistReviewsUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.poi.storage.SpecialistReviewRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistReviewsUseCaseImpl implements GetSpecialistReviewsUseCase {

    private final SpecialistReviewRepository reviewRepository;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        if (!StringUtils.hasText(request.profileId())) {
            context.setError(ErrorCode.FIELD_REQUIRED);
            return new Response(false, context.getErrorMessage(), List.of());
        }

        log.info("[specialistProfileId: {}] List reviews", request.profileId());
        List<ReviewItem> reviews = reviewRepository.findBySpecialistId(request.profileId()).stream()
                .map(review -> new ReviewItem(
                        review.getId(),
                        review.getBookingId(),
                        review.getAuthorId(),
                        review.getScore(),
                        review.getText(),
                        review.getCreatedAt()
                ))
                .toList();
        context.setSuccessResult();
        return new Response(true, context.getErrorMessage(), reviews);
    }
}
