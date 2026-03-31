package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.CommentLike;
import ru.belov.ourabroad.poi.storage.CommentLikeRepository;
import ru.belov.ourabroad.poi.storage.mappers.CommentLikeRowMapper;
import ru.belov.ourabroad.poi.storage.sql.CommentLikeSql;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final CommentLikeRowMapper rowMapper;

    @Override
    public Optional<CommentLike> findByUserIdAndCommentId(String userId, String commentId) {
        return jdbc.query(
                        CommentLikeSql.FIND_BY_USER_AND_COMMENT,
                        Map.of("userId", userId, "commentId", commentId),
                        rowMapper
                )
                .stream()
                .findFirst();
    }

    @Override
    public void save(CommentLike like) {
        jdbc.update(
                CommentLikeSql.INSERT,
                Map.of("id", like.id(), "userId", like.userId(), "commentId", like.commentId())
        );
    }

    @Override
    public void deleteByUserIdAndCommentId(String userId, String commentId) {
        jdbc.update(CommentLikeSql.DELETE_BY_USER_AND_COMMENT, Map.of("userId", userId, "commentId", commentId));
    }
}
