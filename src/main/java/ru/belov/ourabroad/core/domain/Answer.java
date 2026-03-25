package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class Answer {

    private final String id;
    private final String questionId;
    private final String authorId;
    private String content;
    private int votes;
    private boolean accepted;
    private final LocalDateTime createdAt;

    private Answer(
            String id,
            String questionId,
            String authorId,
            String content,
            int votes,
            boolean accepted,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.questionId = questionId;
        this.authorId = authorId;
        this.content = content;
        this.votes = votes;
        this.accepted = accepted;
        this.createdAt = createdAt;
    }

    public static Answer create(
            String id,
            String questionId,
            String authorId,
            String content,
            int votes,
            boolean accepted,
            LocalDateTime createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(questionId);
        Objects.requireNonNull(authorId);
        Objects.requireNonNull(content);
        LocalDateTime ts = createdAt != null ? createdAt : LocalDateTime.now();
        return new Answer(id, questionId, authorId, content, votes, accepted, ts);
    }
}
