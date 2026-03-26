package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Answer;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository {

    void save(Answer answer);

    Optional<Answer> findById(String id);

    List<Answer> findByQuestionId(String questionId);

    Optional<Answer> findAcceptedByQuestionId(String questionId);

    void clearAcceptedByQuestionId(String questionId);

    void setAccepted(String answerId, boolean accepted);

    boolean addVoteDelta(String answerId, int delta);
}
