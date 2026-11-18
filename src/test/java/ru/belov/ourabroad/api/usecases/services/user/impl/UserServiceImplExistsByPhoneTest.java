package ru.belov.ourabroad.api.usecases.services.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.poi.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplExistsByPhoneTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void WHEN_otherUserHasPhone_THEN_returnsTrue() {
        when(userRepository.existsByPhone("me", "+79001112233")).thenReturn(true);

        boolean exists = userService.existsByPhone("me", "+79001112233", new Context());

        assertThat(exists).isTrue();
        verify(userRepository).existsByPhone("me", "+79001112233");
    }

    @Test
    void WHEN_onlySelfHasPhone_THEN_returnsFalse() {
        when(userRepository.existsByPhone("me", "+79001112233")).thenReturn(false);

        boolean exists = userService.existsByPhone("me", "+79001112233", new Context());

        assertThat(exists).isFalse();
        verify(userRepository).existsByPhone("me", "+79001112233");
    }
}
