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
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.create.specialistprofile.CreateSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByUserIdUseCase;

@RestController
@RequestMapping("/api/specialist-profiles")
@RequiredArgsConstructor
@Slf4j
public class SpecialistProfileController {

    private final CreateSpecialistProfileUseCase createSpecialistProfileUseCase;
    private final GetSpecialistProfileByIdUseCase getSpecialistProfileByIdUseCase;
    private final GetSpecialistProfileByUserIdUseCase getSpecialistProfileByUserIdUseCase;
    private final ChangeSpecialistProfileUseCase changeSpecialistProfileUseCase;

    @PostMapping
    public ResponseEntity<CreateSpecialistProfileUseCase.Response> create(
            @RequestBody CreateSpecialistProfileUseCase.Request request
    ) {
        log.info("[userId: {}] Request to create specialist profile", request.userId());
        var response = createSpecialistProfileUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-id")
    public ResponseEntity<GetSpecialistProfileByIdUseCase.Response> getById(
            @RequestParam("profileId") String profileId
    ) {
        log.info("[profileId: {}] Request to get specialist profile by id", profileId);
        var request = new GetSpecialistProfileByIdUseCase.Request(profileId);
        var response = getSpecialistProfileByIdUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user")
    public ResponseEntity<GetSpecialistProfileByUserIdUseCase.Response> getByUserId(
            @RequestParam("userId") String userId
    ) {
        log.info("[userId: {}] Request to get specialist profile by user id", userId);
        var request = new GetSpecialistProfileByUserIdUseCase.Request(userId);
        var response = getSpecialistProfileByUserIdUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<ChangeSpecialistProfileUseCase.Response> update(
            @RequestBody ChangeSpecialistProfileUseCase.Request request
    ) {
        log.info("[profileId: {}] Request to change specialist profile", request.profileId());
        var response = changeSpecialistProfileUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}

