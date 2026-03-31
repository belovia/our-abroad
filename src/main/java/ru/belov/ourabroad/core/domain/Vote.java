package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;
import ru.belov.ourabroad.core.enums.VoteType;

import java.util.Objects;

@Getter
@Setter
public class Vote {

    private final String id;
    private final String userId;
    private final String entityId;
    private VoteType type;

    private Vote(String id, String userId, String entityId, VoteType type) {
        this.id = id;
        this.userId = userId;
        this.entityId = entityId;
        this.type = type;
    }

    public static Vote create(String id, String userId, String entityId, VoteType type) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(entityId);
        Objects.requireNonNull(type);
        return new Vote(id, userId, entityId, type);
    }
}
