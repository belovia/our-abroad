package ru.belov.ourabroad.api.usecases.services.user;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

public interface UserService {

    User findById(String userId, Context context);
    User findByEmail(String userId, String userEmail, Context context);
    User findByPhone(String userId, String userPhone, Context context);
    boolean update(User user, Context context);
}
