package ru.belov.ourabroad.api.usecases.get.user;

import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.web.dto.UserProfileView;

public interface GetUserProfileUseCase {
    Profile getByUserId(String userId);
}
