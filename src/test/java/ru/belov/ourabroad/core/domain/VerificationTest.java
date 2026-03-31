package ru.belov.ourabroad.core.domain;

import org.junit.jupiter.api.Test;
import ru.belov.ourabroad.core.enums.VerificationStatus;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTest {

    @Test
    void verify_setsVerifiedStatusAndTimestamp() {
        Verification v = Verification.create(
                "v1", "u1", VerificationType.EMAIL, null,
                VerificationStatus.PENDING,
                LocalDateTime.now().minusDays(1),
                null
        );
        assertThat(v.isPending()).isTrue();

        v.verify();

        assertThat(v.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(v.getVerifiedAt()).isNotNull();
    }

    @Test
    void reject_setsRejectedStatusAndTimestamp() {
        Verification v = Verification.create(
                "v1", "u1", VerificationType.DOCUMENT, "doc-1",
                VerificationStatus.PENDING,
                LocalDateTime.now(),
                null
        );

        v.reject();

        assertThat(v.getStatus()).isEqualTo(VerificationStatus.REJECTED);
        assertThat(v.getVerifiedAt()).isNotNull();
    }

    @Test
    void isPending_falseWhenNotPending() {
        Verification v = Verification.create(
                "v1", "u1", VerificationType.PHONE, null,
                VerificationStatus.VERIFIED,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertThat(v.isPending()).isFalse();
    }
}
