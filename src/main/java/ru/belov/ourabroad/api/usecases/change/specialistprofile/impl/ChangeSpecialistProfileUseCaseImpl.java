package ru.belov.ourabroad.api.usecases.change.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.web.dto.change.ChangeSpecialistProfileRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistProfileUseCaseImpl implements ChangeSpecialistProfileUseCase {

    private final SpecialistProfileRepository repository;

    @Override
    public void changeProfile(String profileId, ChangeSpecialistProfileRequest request) {
        
    }
}
