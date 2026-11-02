package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.qa.AnswerQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.CreateQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.GetAnswersByQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.GetQuestionByIdUseCase;
import ru.belov.ourabroad.api.usecases.qa.GetQuestionsUseCase;
import ru.belov.ourabroad.api.usecases.qa.VoteUseCase;
import ru.belov.ourabroad.web.dto.qa.CreateAnswerBody;
import ru.belov.ourabroad.web.dto.qa.CreateQuestionBody;
import ru.belov.ourabroad.web.dto.qa.VoteBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class QaController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final AnswerQuestionUseCase answerQuestionUseCase;
    private final VoteUseCase voteUseCase;
    private final GetQuestionsUseCase getQuestionsUseCase;
    private final GetQuestionByIdUseCase getQuestionByIdUseCase;
    private final GetAnswersByQuestionUseCase getAnswersByQuestionUseCase;

    @PostMapping("/questions")
    public ResponseEntity<CreateQuestionUseCase.Response> createQuestion(
            @RequestBody CreateQuestionBody body
    ) {
        log.info("POST /questions");
        var request = new CreateQuestionUseCase.Request(
                body.title(),
                body.content(),
                body.tags()
        );
        return ResponseEntity.ok(createQuestionUseCase.execute(request));
    }

    @GetMapping("/questions")
    public ResponseEntity<GetQuestionsUseCase.Response> getQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) GetQuestionsUseCase.SortMode sort,
            @RequestParam(required = false) String tag
    ) {
        log.info("GET /questions");
        return ResponseEntity.ok(getQuestionsUseCase.execute(new GetQuestionsUseCase.Request(page, size, sort, tag)));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<GetQuestionByIdUseCase.Response> getQuestionById(@PathVariable("id") String questionId) {
        log.info("[questionId: {}] GET /questions/{id}", questionId);
        return ResponseEntity.ok(getQuestionByIdUseCase.execute(new GetQuestionByIdUseCase.Request(questionId)));
    }

    @GetMapping("/questions/{id}/answers")
    public ResponseEntity<GetAnswersByQuestionUseCase.Response> getAnswers(@PathVariable("id") String questionId) {
        log.info("[questionId: {}] GET /questions/{id}/answers", questionId);
        return ResponseEntity.ok(getAnswersByQuestionUseCase.execute(new GetAnswersByQuestionUseCase.Request(questionId)));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerQuestionUseCase.Response> createAnswer(
            @PathVariable("questionId") String questionId,
            @RequestBody CreateAnswerBody body
    ) {
        log.info("[questionId: {}][userId: {}] POST answer", questionId, body.specialistProfileId());
        var request = new AnswerQuestionUseCase.Request(questionId, body.content(), body.specialistProfileId());
        return ResponseEntity.ok(answerQuestionUseCase.execute(request));
    }

    @PostMapping("/votes")
    public ResponseEntity<VoteUseCase.Response> vote(@RequestBody VoteBody body) {
        log.info("POST /votes");
        var request = new VoteUseCase.Request(
                body.target(),
                body.entityId(),
                body.voteType()
        );
        return ResponseEntity.ok(voteUseCase.execute(request));
    }
}
