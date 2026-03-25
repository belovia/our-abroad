package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.belov.ourabroad.core.enums.VerificationStatus;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class Verification {

    private final String id;
    private final String userId;
    private final VerificationType type;
    private final String relatedEntityId;

    private VerificationStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime verifiedAt;

    private Verification(String id,
                         String userId,
                         VerificationType type,
                         String relatedEntityId,
                         LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = createdAt;
    }

    public static Verification create(
            String id,
            String userId,
            VerificationType type,
            String relatedEntityId,
            VerificationStatus status,
            LocalDateTime createdAt,
            LocalDateTime verifiedAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);

        Verification v = new Verification(
                id,
                userId,
                type,
                relatedEntityId,
                createdAt != null ? createdAt : LocalDateTime.now()
        );

        v.status = status != null ? status : VerificationStatus.PENDING;
        v.verifiedAt = verifiedAt;
        return v;
    }

    public void verify() {
        this.status = VerificationStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = VerificationStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return status == VerificationStatus.PENDING;
    }
}