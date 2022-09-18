package de.doubleslash.quiz.engine.controller;

import de.doubleslash.quiz.engine.dto.Quiz;
import de.doubleslash.quiz.engine.dto.SessionId;
import de.doubleslash.quiz.engine.repository.QuizRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

  private final QuizHandler quizHandler;

  private final QuizRepository repo;

  @GetMapping
  public List<Quiz> getAllQuiz() {
    return StreamSupport.stream(repo.findAll().spliterator(), false)
        .map(q -> Quiz.builder()
            .name(q.getName())
            .id(q.getId())
            .build())
        .collect(Collectors.toList());
  }

  @PostMapping("/{quizId}")
  public ResponseEntity<SessionId> createNewQuiz(@PathVariable(value = "quizId") Long quizId) {
    var sessionId = quizHandler.newQuiz(quizId);
    if (StringUtils.hasText(sessionId)) {
      return new ResponseEntity<>(new SessionId(sessionId), HttpStatus.CREATED);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
