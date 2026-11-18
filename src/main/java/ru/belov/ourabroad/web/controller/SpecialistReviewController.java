package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.create.review.CreateSpecialistReviewUseCase;
import ru.belov.ourabroad.api.usecases.get.review.GetSpecialistReviewsUseCase;

@RestController
@RequestMapping("/api/specialist-reviews")
@RequiredArgsConstructor
@Slf4j
public class SpecialistReviewController {

    private final CreateSpecialistReviewUseCase createSpecialistReviewUseCase;
    private final GetSpecialistReviewsUseCase getSpecialistReviewsUseCase;

    @PostMapping
    public ResponseEntity<CreateSpecialistReviewUseCase.Response> create(@RequestBody CreateBody body) {
        log.info("[bookingId: {}] POST /api/specialist-reviews", body.bookingId());
        return ResponseEntity.ok(createSpecialistReviewUseCase.execute(
                new CreateSpecialistReviewUseCase.Request(body.bookingId(), body.score(), body.text())
        ));
    }

    @GetMapping
    public ResponseEntity<GetSpecialistReviewsUseCase.Response> list(@RequestParam("profileId") String profileId) {
        log.info("[specialistProfileId: {}] GET /api/specialist-reviews", profileId);
        return ResponseEntity.ok(getSpecialistReviewsUseCase.execute(
                new GetSpecialistReviewsUseCase.Request(profileId)
        ));
    }

    public record CreateBody(String bookingId, int score, String text) {
    }
}
