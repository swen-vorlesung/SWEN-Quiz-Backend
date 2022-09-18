package de.doubleslash.quiz.engine.controller;

import de.doubleslash.quiz.engine.dto.NickName;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sessions/{sessionId}/participants")
@RequiredArgsConstructor
public class ParticipantController {

  private final QuizHandler quizHandler;

  @PostMapping
  public HttpStatus addParticipant(@RequestBody @NotNull NickName nickName,
      @PathVariable(value = "sessionId") String sessionId) {

    var success = quizHandler.addParticipant(sessionId, nickName.getNickname());
    if (success) {
      return HttpStatus.CREATED;
    }
    return HttpStatus.BAD_REQUEST;
  }

  @PostMapping("/{nickName}/answers/{answerId}")
  public HttpStatus addParticipant(@PathVariable(value = "sessionId") String sessionId,
      @PathVariable(value = "nickName") String nickName,
      @PathVariable(value = "answerId") Long answerId
  ) {

    var success = quizHandler.addParticipantInput(nickName, sessionId, answerId);
    if (success) {
      return HttpStatus.ACCEPTED;
    }
    return HttpStatus.BAD_REQUEST;
  }
}
