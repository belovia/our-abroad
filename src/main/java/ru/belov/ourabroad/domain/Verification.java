package ru.belov.ourabroad.domain;

import ru.belov.ourabroad.enums.VerificationStatus;
import ru.belov.ourabroad.enums.VerificationType;

import java.time.Instant;

public class Verification {

    private final String id;
    private final String userId;
    private final VerificationType type;
    private final String relatedEntityId;
    private VerificationStatus status;
    private final Instant createdAt;
    private Instant verifiedAt;


    public Verification(
            String id,
            String userId,
            VerificationType type,
            String relatedEntityId
    ) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.relatedEntityId = relatedEntityId;
        this.status = VerificationStatus.PENDING;
        this.createdAt = Instant.now();
    }


    public void verify() {
        this.status = VerificationStatus.VERIFIED;
        this.verifiedAt = Instant.now();
    }


// getters
}