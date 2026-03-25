package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.change.reputation.AddReputationPointsUseCase;
import ru.belov.ourabroad.api.usecases.create.reputation.CreateReputationUseCase;
import ru.belov.ourabroad.api.usecases.get.reputation.GetReputationByUserIdUseCase;

@RestController
@RequestMapping("/api/reputations")
@RequiredArgsConstructor
@Slf4j
public class ReputationController {

    private final CreateReputationUseCase createReputationUseCase;
    private final GetReputationByUserIdUseCase getReputationByUserIdUseCase;
    private final AddReputationPointsUseCase addReputationPointsUseCase;

    @PostMapping
    public ResponseEntity<CreateReputationUseCase.Response> create(
            @RequestBody CreateReputationUseCase.Request request
    ) {
        log.info("[userId: {}] Request to create reputation", request.userId());
        return ResponseEntity.ok(createReputationUseCase.execute(request));
    }

    @GetMapping
    public ResponseEntity<GetReputationByUserIdUseCase.Response> getByUserId(
            @RequestParam("userId") String userId
    ) {
        log.info("[userId: {}] Request to get reputation", userId);
        var request = new GetReputationByUserIdUseCase.Request(userId);
        return ResponseEntity.ok(getReputationByUserIdUseCase.execute(request));
    }

    @PatchMapping("/points")
    public ResponseEntity<AddReputationPointsUseCase.Response> addPoints(
            @RequestBody AddReputationPointsUseCase.Request request
    ) {
        log.info("[userId: {}] Request to add reputation points", request.userId());
        return ResponseEntity.ok(addReputationPointsUseCase.execute(request));
    }
}
