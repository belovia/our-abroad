package ru.belov.ourabroad.api.usecases.services.specialistprofile;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;

public interface SpecialistProfileService {

    SpecialistProfile findById(String profileId, Context context);
    SpecialistProfile findByUserId(String userId, Context context);

    void update(SpecialistProfile specialist);
}
