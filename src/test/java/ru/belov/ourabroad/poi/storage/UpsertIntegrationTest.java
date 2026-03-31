package ru.belov.ourabroad.poi.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class UpsertIntegrationTest {

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
    NamedParameterJdbcTemplate jdbc;

    @Test
    void userSave_shouldUpsert_andNotChangeCreatedAt() {
        LocalDateTime createdAt1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime createdAt2 = LocalDateTime.of(2025, 2, 2, 11, 0);

        User initial = UserFactory.fromDb(
                "u-1",
                "a@example.com",
                "+79990000000",
                "hash1",
                UserStatus.ACTIVE,
                "tg1",
                "wa1",
                "act1",
                createdAt1,
                null
        );
        userRepository.save(initial);

        User updated = UserFactory.fromDb(
                "u-1",
                "b@example.com",
                "+79991111111",
                "hash2",
                UserStatus.ACTIVE,
                "tg2",
                "wa2",
                "act2",
                createdAt2,
                LocalDateTime.of(2025, 3, 3, 12, 0)
        );
        userRepository.save(updated);

        Map<String, Object> row = jdbc.queryForMap(
                "select email, created_at from users where id = :id",
                Map.of("id", "u-1")
        );

        assertEquals("b@example.com", row.get("email"));
        assertNotNull(row.get("created_at"));
        // created_at must be preserved from the first insert
        assertEquals(createdAt1, ((java.sql.Timestamp) row.get("created_at")).toLocalDateTime());
    }
}

