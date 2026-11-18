package ru.belov.ourabroad.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.create.specialistprofile.CreateSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.delete.specialistprofile.DeleteSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByUserIdUseCase;
import ru.belov.ourabroad.config.security.JwtAccessDeniedHandler;
import ru.belov.ourabroad.config.security.JwtAuthenticationEntryPoint;
import ru.belov.ourabroad.config.security.JwtAuthenticationFilter;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.config.security.SecurityConfig;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialistProfileController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class SpecialistProfileControllerSecurityTest {

    private static final String PROFILE_ID = "sp-1";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CreateSpecialistProfileUseCase createSpecialistProfileUseCase;

    @MockitoBean
    private GetSpecialistProfileByIdUseCase getSpecialistProfileByIdUseCase;

    @MockitoBean
    private GetSpecialistProfileByUserIdUseCase getSpecialistProfileByUserIdUseCase;

    @MockitoBean
    private ChangeSpecialistProfileUseCase changeSpecialistProfileUseCase;

    @MockitoBean
    private DeleteSpecialistProfileUseCase deleteSpecialistProfileUseCase;

    @Test
    void WHEN_guestDeletes_THEN_401Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/specialist-profiles/{profileId}", PROFILE_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));

        verify(deleteSpecialistProfileUseCase, never()).delete(PROFILE_ID);
    }

    @Test
    void WHEN_authenticatedOwnerDeletes_THEN_200AndUseCaseCalled() throws Exception {
        when(deleteSpecialistProfileUseCase.delete(PROFILE_ID))
                .thenReturn(new DeleteSpecialistProfileUseCase.Response(true, ErrorCode.SUCCESS.getMessage()));

        mockMvc.perform(delete("/api/specialist-profiles/{profileId}", PROFILE_ID)
                        .with(user("user-1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(deleteSpecialistProfileUseCase).delete(PROFILE_ID);
    }

    @Test
    void WHEN_useCaseFails_THEN_400() throws Exception {
        when(deleteSpecialistProfileUseCase.delete(PROFILE_ID))
                .thenReturn(new DeleteSpecialistProfileUseCase.Response(
                        false, ErrorCode.ACCESS_DENIED.getMessage()
                ));

        mockMvc.perform(delete("/api/specialist-profiles/{profileId}", PROFILE_ID)
                        .with(user("user-1").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ACCESS_DENIED.getMessage()));

        verify(deleteSpecialistProfileUseCase).delete(PROFILE_ID);
    }
}
