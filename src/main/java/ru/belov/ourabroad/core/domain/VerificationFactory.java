package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.enums.VerificationStatus;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public final class VerificationFactory {

    private VerificationFactory() {}

    public static Verification fromDb(
            String id,
            String userId,
            VerificationType type,
            String relatedEntityId,
            VerificationStatus status,
            LocalDateTime createdAt,
            LocalDateTime verifiedAt
    ) {
        return Verification.create(
                id,
                userId,
                type,
                relatedEntityId,
                status,
                createdAt,
                verifiedAt
        );
    }

    public static Verification newVerification(
            String id,
            String userId,
            VerificationType type,
            String relatedEntityId
    ) {
        return Verification.create(
                id,
                userId,
                type,
                relatedEntityId,
                VerificationStatus.PENDING,
                LocalDateTime.now(),
                null
        );
    }
}
