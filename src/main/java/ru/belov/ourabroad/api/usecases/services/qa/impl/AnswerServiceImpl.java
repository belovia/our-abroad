package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.AcceptAnswerResult;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.QuestionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.ANSWER_NOT_FOUND;
import static ru.belov.ourabroad.web.validators.ErrorCode.ENTITY_VOTE_UPDATE_FAILED;
import static ru.belov.ourabroad.web.validators.ErrorCode.PERMISSION_DENIED;
import static ru.belov.ourabroad.web.validators.ErrorCode.QUESTION_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final SpecialistProfileService specialistProfileService;

    @Override
    public void createAnswer(Answer answer, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[answerId: {}] Saving answer for question {}", answer.getId(), answer.getQuestionId());
        if (answer.getSpecialistProfileId() != null) {
            SpecialistProfile profile = specialistProfileService.findById(answer.getSpecialistProfileId(), context);
            if (!context.isSuccess() || profile == null) {
                return;
            }
            if (!answer.getAuthorId().equals(profile.getUserId())) {
                log.warn(
                        "[answerId: {}][userId: {}][specialistProfileId: {}] SpecialistProfile doesn't belong to author",
                        answer.getId(),
                        answer.getAuthorId(),
                        answer.getSpecialistProfileId()
                );
                context.setError(PERMISSION_DENIED);
                return;
            }
        }
        answerRepository.save(answer);
    }

    @Override
    public List<Answer> findByQuestionId(String questionId, Context context) {
        if (!context.isSuccess()) {
            return List.of();
        }
        log.info("[questionId: {}] Load answers", questionId);
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    public List<Answer> getAnswersSorted(String questionId, Context context) {
        if (!context.isSuccess()) {
            return List.of();
        }
        log.info("[questionId: {}] Load sorted answers", questionId);
        List<Answer> fromDb = answerRepository.findByQuestionIdSorted(questionId, null);
        // Safety net: keep order contract even if storage changes
        return fromDb.stream()
                .sorted(
                        Comparator.comparing(Answer::isAccepted).reversed()
                                .thenComparing(Answer::getVotes, Comparator.reverseOrder())
                                .thenComparing(Answer::getCreatedAt)
                )
                .toList();
    }

    @Override
    public Answer findById(String answerId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info("[answerId: {}] Try to load answer", answerId);
        Optional<Answer> fromDb = answerRepository.findById(answerId);
        if (fromDb.isEmpty()) {
            log.warn("[answerId: {}] Answer not found", answerId);
            context.setError(ANSWER_NOT_FOUND);
            return null;
        }
        return fromDb.get();
    }

    @Override
    public Answer findByIdOrError(String answerId, Context context) {
        return findById(answerId, context);
    }

    @Override
    public void applyVoteDelta(String answerId, int delta, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[answerId: {}] Apply vote delta {}", answerId, delta);
        if (!answerRepository.addVoteDelta(answerId, delta)) {
            log.warn("[answerId: {}] addVoteDelta affected no rows", answerId);
            context.setError(ENTITY_VOTE_UPDATE_FAILED);
        }
    }

    @Override
    public AcceptAnswerResult acceptAnswer(String answerId, String userId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info("[answerId: {}][userId: {}] Accept answer", answerId, userId);

        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            log.warn("[answerId: {}] Answer not found for accept", answerId);
            context.setError(ANSWER_NOT_FOUND);
            return null;
        }
        Answer answer = answerOpt.get();

        Optional<Question> questionOpt = questionRepository.findById(answer.getQuestionId());
        if (questionOpt.isEmpty()) {
            log.warn("[questionId: {}] Question not found for accept", answer.getQuestionId());
            context.setError(QUESTION_NOT_FOUND);
            return null;
        }
        Question question = questionOpt.get();

        if (!userId.equals(question.getAuthorId())) {
            log.warn(
                    "[answerId: {}][questionId: {}][userId: {}] Permission denied to accept answer",
                    answerId,
                    question.getId(),
                    userId
            );
            context.setError(PERMISSION_DENIED);
            return null;
        }

        Optional<Answer> existingAcceptedOpt = answerRepository.findAcceptedByQuestionId(question.getId());
        if (existingAcceptedOpt.isPresent() && existingAcceptedOpt.get().getId().equals(answerId)) {
            log.info("[answerId: {}][questionId: {}] Same accepted answer — no-op", answerId, question.getId());
            return new AcceptAnswerResult(answer.getAuthorId(), question.getAuthorId(), false);
        }

        if (existingAcceptedOpt.isPresent()) {
            log.info(
                    "[questionId: {}] Clearing previous accepted answer {}",
                    question.getId(),
                    existingAcceptedOpt.get().getId()
            );
            answerRepository.clearAcceptedByQuestionId(question.getId());
        }

        log.info("[answerId: {}][questionId: {}] Setting accepted=true", answerId, question.getId());
        answerRepository.setAccepted(answerId, true);
        return new AcceptAnswerResult(answer.getAuthorId(), question.getAuthorId(), true);
    }
}
