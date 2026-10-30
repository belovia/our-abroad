package ru.belov.ourabroad.poi.storage.sql;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileSqlTest {

    @Test
    void sqlTargetsProfileTable() {
        assertThat(ProfileSql.FIND_BY_USER_ID).doesNotContain("profiles");
        assertThat(ProfileSql.INSERT).doesNotContain("profiles");
        assertThat(ProfileSql.FIND_BY_USER_ID).contains("from profile");
        assertThat(ProfileSql.INSERT).contains("into profile");
    }

    @Test
    void joinLanguagesTreatsNullAndEmptyAsBlank() {
        assertThat(ProfileSql.joinLanguages(null)).isEmpty();
        assertThat(ProfileSql.joinLanguages(Set.of())).isEmpty();
        assertThat(ProfileSql.joinLanguages(Set.of("es", "ru"))).contains("es").contains("ru");
    }
}
