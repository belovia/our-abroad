package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.poi.storage.helper.QaTagsHelper;
import ru.belov.ourabroad.poi.storage.mappers.QuestionRowMapper;
import ru.belov.ourabroad.poi.storage.sql.QuestionSql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final QuestionRowMapper rowMapper;

    @Override
    public void save(Question question) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", question.getId());
        params.put("authorId", question.getAuthorId());
        params.put("title", question.getTitle());
        params.put("content", question.getContent());
        params.put("tags", QaTagsHelper.serialize(question.getTags()));
        params.put("votes", question.getVotes());
        params.put("answersCount", question.getAnswersCount());
        params.put("createdAt", Timestamp.valueOf(question.getCreatedAt()));
        jdbc.update(QuestionSql.INSERT, params);
    }

    @Override
    public Optional<Question> findById(String id) {
        return jdbc.query(QuestionSql.FIND_BY_ID, Map.of("id", id), rowMapper).stream().findFirst();
    }

    @Override
    public List<Question> findAll(Pageable pageable, Sort sort) {
        String sql = QuestionSql.FIND_ALL + " " + orderByClause(sort);
        Map<String, Object> params = new HashMap<>();
        if (pageable != null && pageable.isPaged()) {
            sql += " LIMIT :limit OFFSET :offset";
            params.put("limit", pageable.getPageSize());
            params.put("offset", pageable.getOffset());
        }
        return jdbc.query(sql, params, rowMapper);
    }

    @Override
    public List<Question> findByTag(String tag, Pageable pageable, Sort sort) {
        String sql = QuestionSql.FIND_BY_TAG + " " + orderByClause(sort);
        Map<String, Object> params = new HashMap<>();
        params.put("tagLike", "%" + (tag == null ? "" : tag.trim().toLowerCase()) + "%");
        if (pageable != null && pageable.isPaged()) {
            sql += " LIMIT :limit OFFSET :offset";
            params.put("limit", pageable.getPageSize());
            params.put("offset", pageable.getOffset());
        }
        return jdbc.query(sql, params, rowMapper);
    }

    @Override
    public boolean addVoteDelta(String questionId, int delta) {
        return jdbc.update(
                QuestionSql.ADD_VOTE_DELTA,
                Map.of("id", questionId, "delta", delta)
        ) > 0;
    }

    @Override
    public boolean updateVotes(String questionId, int votes) {
        return jdbc.update(
                QuestionSql.UPDATE_VOTES,
                Map.of("id", questionId, "votes", votes)
        ) > 0;
    }

    @Override
    public boolean incrementAnswersCount(String questionId) {
        return jdbc.update(
                QuestionSql.INCREMENT_ANSWERS_COUNT,
                Map.of("id", questionId)
        ) > 0;
    }

    private static String orderByClause(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "ORDER BY created_at DESC";
        }
        List<String> parts = new ArrayList<>();
        for (Sort.Order o : sort) {
            String column = sortColumn(o.getProperty());
            if (column != null) {
                parts.add(column + " " + (o.isAscending() ? "ASC" : "DESC"));
            }
        }
        if (parts.isEmpty()) {
            return "ORDER BY created_at DESC";
        }
        return "ORDER BY " + String.join(", ", parts);
    }

    private static String sortColumn(String property) {
        if (property == null) {
            return null;
        }
        return switch (property) {
            case "createdAt" -> "created_at";
            case "votes" -> "votes";
            default -> null;
        };
    }
}
