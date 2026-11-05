package ru.belov.ourabroad.api.usecases.get.review.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.review.GetSpecialistReviewsUseCase;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.poi.storage.SpecialistReviewRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GetSpecialistReviewsUseCaseImpl.class)
class GetSpecialistReviewsUseCaseImplTest {

    private static final String PROFILE_ID = "sp-1";

    @MockitoBean
    private SpecialistReviewRepository reviewRepository;

    @Autowired
    private GetSpecialistReviewsUseCase useCase;

    @Test
    void WHEN_reviewsExist_THEN_returnedNewestFirst() {
        LocalDateTime earlier = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime later = LocalDateTime.of(2026, 2, 1, 10, 0);
        when(reviewRepository.findBySpecialistId(PROFILE_ID)).thenReturn(List.of(
                review("r-2", later),
                review("r-1", earlier)
        ));

        var response = useCase.execute(new GetSpecialistReviewsUseCase.Request(PROFILE_ID));

        assertThat(response.success()).isTrue();
        assertThat(response.reviews()).extracting(GetSpecialistReviewsUseCase.ReviewItem::id)
                .containsExactly("r-2", "r-1");
        assertThat(response.reviews().getFirst().score()).isEqualTo(5);
        assertThat(response.reviews().getFirst().createdAt()).isEqualTo(later);
        verify(reviewRepository).findBySpecialistId(PROFILE_ID);
    }

    @Test
    void WHEN_noReviews_THEN_emptyListAndSuccess() {
        when(reviewRepository.findBySpecialistId(PROFILE_ID)).thenReturn(List.of());

        var response = useCase.execute(new GetSpecialistReviewsUseCase.Request(PROFILE_ID));

        assertThat(response.success()).isTrue();
        assertThat(response.reviews()).isEmpty();
    }

    @Test
    void WHEN_profileIdBlank_THEN_fieldRequired() {
        var response = useCase.execute(new GetSpecialistReviewsUseCase.Request(" "));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        assertThat(response.reviews()).isEmpty();
    }

    private static SpecialistReview review(String id, LocalDateTime createdAt) {
        return SpecialistReview.builder()
                .id(id)
                .bookingId("booking-" + id)
                .authorId("client-1")
                .specialistId(PROFILE_ID)
                .score(5)
                .text("текст")
                .createdAt(createdAt)
                .build();
    }
}
