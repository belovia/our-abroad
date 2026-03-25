package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Question;

import java.util.Optional;

public interface QuestionRepository {

    void save(Question question);

    Optional<Question> findById(String id);

    boolean addVoteDelta(String questionId, int delta);

    boolean updateVotes(String questionId, int votes);

    boolean incrementAnswersCount(String questionId);
}
