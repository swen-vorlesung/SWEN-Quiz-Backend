package de.doubleslash.quiz.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

  private final QuizHandler quizHandler;

  @PostMapping("/{sessionId}/quiz/start")
  public HttpStatus startQuiz(@PathVariable(value = "sessionId") String sessionId) {
    var success = quizHandler.startQuiz(sessionId);
    if (success) {
      return HttpStatus.ACCEPTED;
    }
    return HttpStatus.BAD_REQUEST;
  }

  @PostMapping("/{sessionId}/quiz/next")
  public HttpStatus startNextQuestion(@PathVariable(value = "sessionId") String sessionId) {
    var success = quizHandler.startNewQuestion(sessionId);
    if (success) {
      return HttpStatus.ACCEPTED;
    }
    return HttpStatus.BAD_REQUEST;
  }
}
