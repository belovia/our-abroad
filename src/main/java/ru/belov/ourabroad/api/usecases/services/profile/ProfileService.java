package ru.belov.ourabroad.api.usecases.services.profile;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;

public interface ProfileService {

    void saveEmpty(String userId, Context context);

    Profile findByUserId(String userId, Context context);

    void update(Profile profile, Context context);
}
