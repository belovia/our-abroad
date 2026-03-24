package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.change.specialistservice.ChangeSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.create.specialistservice.CreateSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.delete.specialistservice.DeleteSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;

@RestController
@RequestMapping("/api/specialist-services")
@RequiredArgsConstructor
@Slf4j
public class SpecialistServiceController {

    private final CreateSpecialistServiceUseCase createSpecialistServiceUseCase;
    private final ChangeSpecialistServiceUseCase changeSpecialistServiceUseCase;
    private final DeleteSpecialistServiceUseCase deleteSpecialistServiceUseCase;
    private final GetServicesByProfileIdUseCase getServicesByProfileIdUseCase;

    @PostMapping
    public ResponseEntity<CreateSpecialistServiceUseCase.Response> create(
            @RequestBody CreateSpecialistServiceUseCase.Request request
    ) {
        log.info("[profileId: {}] Request to create specialist service", request.specialistProfileId());
        var response = createSpecialistServiceUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<ChangeSpecialistServiceUseCase.Response> update(
            @RequestBody ChangeSpecialistServiceUseCase.Request request
    ) {
        log.info("[serviceId: {}] Request to change specialist service", request.serviceId());
        var response = changeSpecialistServiceUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(
            @PathVariable("serviceId") String serviceId
    ) {
        log.info("[serviceId: {}] Request to delete specialist service", serviceId);
        deleteSpecialistServiceUseCase.delete(serviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<GetServicesByProfileIdUseCase.Response> listByProfile(
            @RequestParam("profileId") String profileId
    ) {
        log.info("[profileId: {}] Request to list specialist services by profile id", profileId);
        var request = new GetServicesByProfileIdUseCase.Request(profileId);
        var response = getServicesByProfileIdUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}

