package ru.belov.ourabroad.api.usecases.get.specialistprofile;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

public interface GetSpecialistProfileUseCase {

    SpecialistProfile getByUserId(String userId);

    SpecialistProfile getById(String profileId);
}
