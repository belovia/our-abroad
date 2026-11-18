package ru.belov.ourabroad.poi.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.core.domain.SpecialistReviewFactory;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.core.security.AppRoles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class SpecialistReviewRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    SpecialistProfileRepository specialistProfileRepository;
    @Autowired
    SpecialistServiceRepository specialistServiceRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    SpecialistReviewRepository reviewRepository;

    @Test
    void insert_uniqueBooking_averageAndOrder() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 1, 9, 0);
        userRepository.save(user("client-1", "c1@example.com", "+79990000011", now));
        userRepository.save(user("client-2", "c2@example.com", "+79990000012", now));
        userRepository.save(user("spec-user-1", "s1@example.com", "+79990000013", now));

        specialistProfileRepository.save(SpecialistProfile.builder()
                .id("sp-1").userId("spec-user-1").description("desc")
                .active(true).rating(0).reviewsCount(0).build());
        specialistServiceRepository.save(new SpecialistService(
                "srv-1", "sp-1", "Consultation", "30 min", 50, "USD", true));

        Booking first = BookingFactory.create("client-1", "sp-1", "srv-1", now.plusDays(1));
        Booking second = BookingFactory.create("client-2", "sp-1", "srv-1", now.plusDays(2));
        bookingRepository.save(first);
        bookingRepository.save(second);

        SpecialistReview earlier = SpecialistReviewFactory.create(first.getId(), "client-1", "sp-1", 4, "хорошо");
        reviewRepository.save(earlier);
        assertThat(reviewRepository.existsByBookingId(first.getId())).isTrue();

        assertThatThrownBy(() -> reviewRepository.save(
                SpecialistReviewFactory.create(first.getId(), "client-1", "sp-1", 5, "повтор")
        )).isInstanceOf(DuplicateKeyException.class);

        SpecialistReview later = SpecialistReviewFactory.create(second.getId(), "client-2", "sp-1", 5, "отлично");
        reviewRepository.save(later);

        SpecialistReviewRepository.ReviewStats stats = reviewRepository.averageScoreAndCount("sp-1");
        assertThat(stats.reviewsCount()).isEqualTo(2);
        assertThat(stats.rating()).isEqualTo(4.5);

        List<SpecialistReview> listed = reviewRepository.findBySpecialistId("sp-1");
        assertThat(listed).extracting(SpecialistReview::getId)
                .containsExactly(later.getId(), earlier.getId());
    }

    private static User user(String id, String email, String phone, LocalDateTime createdAt) {
        return UserFactory.fromDb(
                id, email, phone, "hash", UserStatus.ACTIVE,
                null, null, null, AppRoles.DEFAULT, createdAt, null
        );
    }
}
