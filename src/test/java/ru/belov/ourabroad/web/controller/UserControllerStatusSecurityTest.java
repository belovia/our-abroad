package ru.belov.ourabroad.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.belov.ourabroad.api.usecases.change.profile.UpdateMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ActivateUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.BlockUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserEmailUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPhoneUseCase;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.get.profile.GetMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByIdUsecase;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByPhoneUseCase;
import ru.belov.ourabroad.api.usecases.update.UserUpdateUsecase;
import ru.belov.ourabroad.config.security.JwtAccessDeniedHandler;
import ru.belov.ourabroad.config.security.JwtAuthenticationEntryPoint;
import ru.belov.ourabroad.config.security.JwtAuthenticationFilter;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.config.security.SecurityConfig;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class UserControllerStatusSecurityTest {

    private static final String USER_ID = "user-1";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private BlockUserUseCase blockUserUseCase;

    @MockitoBean
    private ActivateUserUseCase activateUserUseCase;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;
    @MockitoBean
    private UserUpdateUsecase userUpdateUsecase;
    @MockitoBean
    private GetUserByIdUsecase getUserByIdUsecase;
    @MockitoBean
    private GetUserByEmailUseCase getUserByEmailUseCase;
    @MockitoBean
    private GetUserByPhoneUseCase getUserByPhoneUseCase;
    @MockitoBean
    private ChangeUserEmailUseCase changeUserEmailUseCase;
    @MockitoBean
    private ChangeUserPasswordUseCase changeUserPasswordUseCase;
    @MockitoBean
    private ChangeUserPhoneUseCase changeUserPhoneUseCase;
    @MockitoBean
    private GetMyProfileUseCase getMyProfileUseCase;
    @MockitoBean
    private UpdateMyProfileUseCase updateMyProfileUseCase;

    @Test
    void WHEN_adminBlocks_THEN_200AndUseCaseCalled() throws Exception {
        when(blockUserUseCase.execute(new BlockUserUseCase.Request(USER_ID)))
                .thenReturn(new BlockUserUseCase.Response(USER_ID, true, ErrorCode.SUCCESS.getMessage()));

        mockMvc.perform(patch("/api/users/{userId}/block", USER_ID).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.success").value(true));

        verify(blockUserUseCase).execute(new BlockUserUseCase.Request(USER_ID));
    }

    @Test
    void WHEN_adminActivates_THEN_200AndUseCaseCalled() throws Exception {
        when(activateUserUseCase.execute(new ActivateUserUseCase.Request(USER_ID)))
                .thenReturn(new ActivateUserUseCase.Response(USER_ID, true, ErrorCode.SUCCESS.getMessage()));

        mockMvc.perform(patch("/api/users/{userId}/activate", USER_ID).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(activateUserUseCase).execute(new ActivateUserUseCase.Request(USER_ID));
    }

    @Test
    void WHEN_regularUserBlocks_THEN_403AccessDenied() throws Exception {
        mockMvc.perform(patch("/api/users/{userId}/block", USER_ID).with(user("bob").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.getCode()));

        verify(blockUserUseCase, never()).execute(any());
    }

    @Test
    void WHEN_regularUserActivates_THEN_403AccessDenied() throws Exception {
        mockMvc.perform(patch("/api/users/{userId}/activate", USER_ID).with(user("bob").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.getCode()));

        verify(activateUserUseCase, never()).execute(any());
    }
}
