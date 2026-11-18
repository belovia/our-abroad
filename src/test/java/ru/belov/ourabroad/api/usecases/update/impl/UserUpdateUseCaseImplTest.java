package ru.belov.ourabroad.api.usecases.update.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.core.security.AppRoles;
import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserUpdateUseCaseImpl.class, UserValidator.class})
class UserUpdateUseCaseImplTest {

    private static final String USER_ID = "user-123";
    private static final String OWN_PHONE = "+79119999999";
    private static final String OTHER_PHONE = "+79001234567";

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private UserUpdateUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void stubCurrentUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void WHEN_patchOccupiedPhone_THEN_doesNotPersist() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhone(OTHER_PHONE);

        when(userService.existsByPhone(eq(USER_ID), eq(OTHER_PHONE), any(Context.class))).thenReturn(true);

        usecase.updateUser(request);

        verify(userService, never()).findById(any(), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_patchOwnPhone_THEN_persists() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhone(OWN_PHONE);
        User existing = createUser();

        when(userService.existsByPhone(eq(USER_ID), eq(OWN_PHONE), any(Context.class))).thenReturn(false);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existing);
        doNothing().when(userService).update(any(User.class), any(Context.class));

        usecase.updateUser(request);

        verify(userService).update(userCaptor.capture(), any(Context.class));
        assertThat(userCaptor.getValue().getPhone()).isEqualTo(OWN_PHONE);
    }

    private User createUser() {
        return User.create(
                USER_ID, "user@example.com", OWN_PHONE, "password",
                UserStatus.ACTIVE, null, null, null,
                AppRoles.DEFAULT,
                LocalDateTime.now(), null
        );
    }
}
