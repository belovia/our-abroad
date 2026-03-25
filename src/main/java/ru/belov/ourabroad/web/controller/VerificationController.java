package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.change.verification.CompleteVerificationUseCase;
import ru.belov.ourabroad.api.usecases.change.verification.RejectVerificationUseCase;
import ru.belov.ourabroad.api.usecases.create.verification.CreateVerificationUseCase;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationByIdUseCase;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationsByUserIdUseCase;

@RestController
@RequestMapping("/api/verifications")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final CreateVerificationUseCase createVerificationUseCase;
    private final GetVerificationByIdUseCase getVerificationByIdUseCase;
    private final GetVerificationsByUserIdUseCase getVerificationsByUserIdUseCase;
    private final CompleteVerificationUseCase completeVerificationUseCase;
    private final RejectVerificationUseCase rejectVerificationUseCase;

    @PostMapping
    public ResponseEntity<CreateVerificationUseCase.Response> create(
            @RequestBody CreateVerificationUseCase.Request request
    ) {
        log.info("[userId: {}] Request to create verification", request.userId());
        return ResponseEntity.ok(createVerificationUseCase.execute(request));
    }

    @GetMapping("/by-id")
    public ResponseEntity<GetVerificationByIdUseCase.Response> getById(
            @RequestParam("id") String verificationId
    ) {
        log.info("[verificationId: {}] Request to get verification", verificationId);
        var request = new GetVerificationByIdUseCase.Request(verificationId);
        return ResponseEntity.ok(getVerificationByIdUseCase.execute(request));
    }

    @GetMapping("/by-user")
    public ResponseEntity<GetVerificationsByUserIdUseCase.Response> listByUser(
            @RequestParam("userId") String userId
    ) {
        log.info("[userId: {}] Request to list verifications", userId);
        var request = new GetVerificationsByUserIdUseCase.Request(userId);
        return ResponseEntity.ok(getVerificationsByUserIdUseCase.execute(request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<CompleteVerificationUseCase.Response> complete(
            @PathVariable("id") String verificationId
    ) {
        log.info("[verificationId: {}] Request to complete verification", verificationId);
        var request = new CompleteVerificationUseCase.Request(verificationId);
        return ResponseEntity.ok(completeVerificationUseCase.execute(request));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RejectVerificationUseCase.Response> reject(
            @PathVariable("id") String verificationId
    ) {
        log.info("[verificationId: {}] Request to reject verification", verificationId);
        var request = new RejectVerificationUseCase.Request(verificationId);
        return ResponseEntity.ok(rejectVerificationUseCase.execute(request));
    }
}
