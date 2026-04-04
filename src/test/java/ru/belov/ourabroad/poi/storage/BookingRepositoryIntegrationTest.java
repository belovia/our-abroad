package ru.belov.ourabroad.poi.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.core.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class BookingRepositoryIntegrationTest {

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

    @Test
    void insert_updateStatus_findByUserId() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 1, 9, 0);
        User client = UserFactory.fromDb(
                "client-1",
                "client1@example.com",
                "+79990000001",
                "hash",
                UserStatus.ACTIVE,
                null,
                null,
                null,
                now,
                null
        );
        User specialistUser = UserFactory.fromDb(
                "spec-user-1",
                "spec1@example.com",
                "+79990000002",
                "hash",
                UserStatus.ACTIVE,
                null,
                null,
                null,
                now,
                null
        );
        userRepository.save(client);
        userRepository.save(specialistUser);

        SpecialistProfile profile = SpecialistProfile.builder()
                .id("sp-1")
                .userId("spec-user-1")
                .description("desc")
                .active(true)
                .rating(0)
                .reviewsCount(0)
                .build();
        specialistProfileRepository.save(profile);

        SpecialistService service = new SpecialistService(
                "srv-1",
                "sp-1",
                "Consultation",
                "30 min",
                50,
                "USD",
                true
        );
        specialistServiceRepository.save(service);

        Booking booking = BookingFactory.create("client-1", "sp-1", "srv-1", LocalDateTime.of(2026, 7, 1, 15, 0));
        bookingRepository.save(booking);

        Optional<Booking> loaded = bookingRepository.findById(booking.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(BookingStatus.PENDING);

        boolean updated = bookingRepository.updateStatus(booking.getId(), BookingStatus.CONFIRMED);
        assertThat(updated).isTrue();
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getStatus())
                .isEqualTo(BookingStatus.CONFIRMED);

        List<Booking> byUser = bookingRepository.findByUserId("client-1");
        assertThat(byUser).hasSize(1);
        assertThat(byUser.getFirst().getId()).isEqualTo(booking.getId());
    }
}
