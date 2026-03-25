package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.qa.AnswerQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.CreateQuestionUseCase;
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

    @PostMapping("/questions")
    public ResponseEntity<CreateQuestionUseCase.Response> createQuestion(
            @RequestBody CreateQuestionBody body
    ) {
        log.info("[userId: {}] POST /questions", body.authorId());
        var request = new CreateQuestionUseCase.Request(
                body.authorId(),
                body.title(),
                body.content(),
                body.tags()
        );
        return ResponseEntity.ok(createQuestionUseCase.execute(request));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerQuestionUseCase.Response> createAnswer(
            @PathVariable("questionId") String questionId,
            @RequestBody CreateAnswerBody body
    ) {
        log.info("[questionId: {}][userId: {}] POST answer", questionId, body.authorId());
        var request = new AnswerQuestionUseCase.Request(questionId, body.authorId(), body.content());
        return ResponseEntity.ok(answerQuestionUseCase.execute(request));
    }

    @PostMapping("/votes")
    public ResponseEntity<VoteUseCase.Response> vote(@RequestBody VoteBody body) {
        log.info("[userId: {}] POST /votes", body.voterUserId());
        var request = new VoteUseCase.Request(
                body.voterUserId(),
                body.target(),
                body.entityId(),
                body.voteType()
        );
        return ResponseEntity.ok(voteUseCase.execute(request));
    }
}
