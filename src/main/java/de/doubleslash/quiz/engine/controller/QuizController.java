package de.doubleslash.quiz.engine.controller;

import de.doubleslash.quiz.engine.web.QuizObserver;
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
@RequestMapping("/sessions/{sessionId}")
@RequiredArgsConstructor
public class QuizController {

  private final QuizObserver quizObserver;

  @PostMapping
  public HttpStatus createNewQuiz(@RequestBody Long quizId,
      @PathVariable(value = "sessionId") String sessionId) {
    var success = quizObserver.newQuiz(quizId, sessionId);
    if (success) {
      return HttpStatus.CREATED;
    }
    return HttpStatus.NOT_FOUND;
  }

  @PostMapping("/quiz/start")
  public HttpStatus startQuiz(@PathVariable(value = "sessionId") String sessionId) {
    var success = quizObserver.startQuiz(sessionId);
    if (success) {
      return HttpStatus.ACCEPTED;
    }
    return HttpStatus.BAD_REQUEST;
  }

  @PostMapping("/quiz/next")
  public HttpStatus startNextQuestion(@PathVariable(value = "sessionId") String sessionId) {
    var success = quizObserver.startNewQuestion(sessionId);
    if (success) {
      return HttpStatus.ACCEPTED;
    }
    return HttpStatus.BAD_REQUEST;
  }
}
