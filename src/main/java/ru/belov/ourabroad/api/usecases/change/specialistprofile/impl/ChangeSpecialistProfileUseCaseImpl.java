package ru.belov.ourabroad.api.usecases.change.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistProfileUseCaseImpl implements ChangeSpecialistProfileUseCase {

    private final SpecialistProfileService service;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String profileId = request.profileId();

        SpecialistProfile fromDb = service.findById(profileId, context);

        fromDb.setDescription(request.description());

        service.update(fromDb);


    }
}
