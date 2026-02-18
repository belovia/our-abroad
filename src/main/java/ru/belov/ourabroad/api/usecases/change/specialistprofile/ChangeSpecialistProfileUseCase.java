package ru.belov.ourabroad.api.usecases.change.specialistprofile;

import ru.belov.ourabroad.web.dto.change.ChangeSpecialistProfileRequest;

public interface ChangeSpecialistProfileUseCase {

    void changeProfile(String profileId, ChangeSpecialistProfileRequest request);
}
