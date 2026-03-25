package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.mappers.AnswerRowMapper;
import ru.belov.ourabroad.poi.storage.sql.AnswerSql;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final AnswerRowMapper rowMapper;

    @Override
    public void save(Answer answer) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", answer.getId());
        params.put("questionId", answer.getQuestionId());
        params.put("authorId", answer.getAuthorId());
        params.put("content", answer.getContent());
        params.put("votes", answer.getVotes());
        params.put("accepted", answer.isAccepted());
        params.put("createdAt", Timestamp.valueOf(answer.getCreatedAt()));
        jdbc.update(AnswerSql.INSERT, params);
    }

    @Override
    public Optional<Answer> findById(String id) {
        return jdbc.query(AnswerSql.FIND_BY_ID, Map.of("id", id), rowMapper).stream().findFirst();
    }

    @Override
    public List<Answer> findByQuestionId(String questionId) {
        return jdbc.query(AnswerSql.FIND_BY_QUESTION_ID, Map.of("questionId", questionId), rowMapper);
    }

    @Override
    public boolean addVoteDelta(String answerId, int delta) {
        return jdbc.update(
                AnswerSql.ADD_VOTE_DELTA,
                Map.of("id", answerId, "delta", delta)
        ) > 0;
    }
}
