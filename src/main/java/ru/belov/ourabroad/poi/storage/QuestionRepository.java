package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    void save(Question question);

    Optional<Question> findById(String id);

    List<Question> findAll(org.springframework.data.domain.Pageable pageable, org.springframework.data.domain.Sort sort);

    List<Question> findByTag(String tag, org.springframework.data.domain.Pageable pageable, org.springframework.data.domain.Sort sort);

    boolean addVoteDelta(String questionId, int delta);

    boolean updateVotes(String questionId, int votes);

    boolean incrementAnswersCount(String questionId);
}
