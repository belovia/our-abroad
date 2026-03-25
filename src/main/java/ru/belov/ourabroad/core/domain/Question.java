package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class Question {

    private final String id;
    private final String authorId;
    private String title;
    private String content;
    private Set<String> tags;
    private int votes;
    private int answersCount;
    private final LocalDateTime createdAt;

    private Question(
            String id,
            String authorId,
            String title,
            String content,
            Set<String> tags,
            int votes,
            int answersCount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.votes = votes;
        this.answersCount = answersCount;
        this.createdAt = createdAt;
    }

    public static Question create(
            String id,
            String authorId,
            String title,
            String content,
            Set<String> tags,
            int votes,
            int answersCount,
            LocalDateTime createdAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(authorId, "authorId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Set<String> tagSet = tags == null ? new HashSet<>() : new HashSet<>(tags);
        LocalDateTime ts = createdAt != null ? createdAt : LocalDateTime.now();

        return new Question(id, authorId, title, content, tagSet, votes, answersCount, ts);
    }
}
