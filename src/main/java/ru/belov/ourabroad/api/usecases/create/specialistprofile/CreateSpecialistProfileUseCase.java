package ru.belov.ourabroad.api.usecases.create.specialistprofile;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

public interface CreateSpecialistProfileUseCase {

    SpecialistProfile create(String userId, String description);
}
