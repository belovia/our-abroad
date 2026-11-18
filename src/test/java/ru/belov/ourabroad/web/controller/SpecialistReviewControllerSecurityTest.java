package ru.belov.ourabroad.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.belov.ourabroad.api.usecases.create.review.CreateSpecialistReviewUseCase;
import ru.belov.ourabroad.api.usecases.get.review.GetSpecialistReviewsUseCase;
import ru.belov.ourabroad.config.security.JwtAccessDeniedHandler;
import ru.belov.ourabroad.config.security.JwtAuthenticationEntryPoint;
import ru.belov.ourabroad.config.security.JwtAuthenticationFilter;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.config.security.SecurityConfig;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialistReviewController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class SpecialistReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CreateSpecialistReviewUseCase createSpecialistReviewUseCase;

    @MockitoBean
    private GetSpecialistReviewsUseCase getSpecialistReviewsUseCase;

    @Test
    void WHEN_guestListsReviews_THEN_200() throws Exception {
        when(getSpecialistReviewsUseCase.execute(new GetSpecialistReviewsUseCase.Request("sp-1")))
                .thenReturn(new GetSpecialistReviewsUseCase.Response(true, ErrorCode.SUCCESS.getMessage(), List.of()));

        mockMvc.perform(get("/api/specialist-reviews").param("profileId", "sp-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reviews").isArray());

        verify(getSpecialistReviewsUseCase).execute(new GetSpecialistReviewsUseCase.Request("sp-1"));
    }

    @Test
    void WHEN_guestCreatesReview_THEN_401Unauthorized() throws Exception {
        mockMvc.perform(post("/api/specialist-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-1\",\"score\":4}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));

        verify(createSpecialistReviewUseCase, never()).execute(any());
    }

    @Test
    void WHEN_authenticatedCreatesReview_THEN_200AndUseCaseCalled() throws Exception {
        when(createSpecialistReviewUseCase.execute(new CreateSpecialistReviewUseCase.Request("booking-1", 4, "хорошо")))
                .thenReturn(new CreateSpecialistReviewUseCase.Response("review-1", true, ErrorCode.SUCCESS.getMessage()));

        mockMvc.perform(post("/api/specialist-reviews")
                        .with(user("client-1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-1\",\"score\":4,\"text\":\"хорошо\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value("review-1"))
                .andExpect(jsonPath("$.success").value(true));

        verify(createSpecialistReviewUseCase)
                .execute(new CreateSpecialistReviewUseCase.Request("booking-1", 4, "хорошо"));
    }
}
