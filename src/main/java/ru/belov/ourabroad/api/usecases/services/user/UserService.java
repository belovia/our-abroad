package ru.belov.ourabroad.api.usecases.services.user;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

import java.util.Optional;

public interface UserService {

    User findById(String userId, Context context);

    User findByEmail(String userEmail, Context context);

    /**
     * Поиск по email без записи кода ошибки в {@link Context} (сценарий логина).
     */
    Optional<User> findByEmailRaw(String email);

    User findByPhone(String userId, String userPhone, Context context);

    void save(User user, Context context);

    void update(User user, Context context);

    boolean existsByEmail(String userId, String email, Context context);
}
