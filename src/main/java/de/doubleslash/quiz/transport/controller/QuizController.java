package de.doubleslash.quiz.transport.controller;

import com.google.common.collect.Lists;
import de.doubleslash.quiz.repository.AnswerRepository;
import de.doubleslash.quiz.repository.QuestionRepository;
import de.doubleslash.quiz.repository.QuizRepository;
import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.repository.dao.quiz.Answer;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import de.doubleslash.quiz.transport.dto.AnswerDto;
import de.doubleslash.quiz.transport.dto.QuestionDto;
import de.doubleslash.quiz.transport.dto.QuizDto;
import de.doubleslash.quiz.transport.dto.QuizView;
import de.doubleslash.quiz.transport.dto.SessionId;
import de.doubleslash.quiz.transport.security.SecurityContextService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

  private final QuizHandler quizHandler;

  private final UserRepository userRepository;

  private final QuizRepository quizRepository;

  private final QuestionRepository questionRepository;

  private final AnswerRepository answerRepository;

  private final SecurityContextService securityContext;

  @GetMapping
  public List<QuizView> getAllQuiz() {
    var username = securityContext.getLoggedInUser();

    return userRepository.findByName(username)
        .map(de.doubleslash.quiz.repository.dao.auth.User::getQuizzes)
        .map(list -> list.stream()
            .map(q -> QuizView.builder()
                .name(q.getName())
                .id(q.getId())
                .build())
            .collect(Collectors.toList()))
        .orElse(Lists.newArrayList());
  }

  @PostMapping
  @Transactional
  public ResponseEntity<Object> saveNewQuiz(@RequestBody QuizDto newQuiz) {
    var username = securityContext.getLoggedInUser();
    var user = userRepository.findByName(username);

    if (user.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // Save quiz
    var savedQuiz = quizRepository.save(Quiz.builder()
        .user(user.get())
        .name(newQuiz.getName())
        .build());

    // Save questions
    for (QuestionDto questionDto : newQuiz.getQuestions()) {
      var savedQuestion = questionRepository.save(Question.builder()
          .quiz(savedQuiz)
          .question(questionDto.getQuestion())
          .answerTime(questionDto.getAnswerTime())
          .build());

      for (AnswerDto answerDto : questionDto.getAnswers()) {
        answerRepository.save(Answer.builder()
            .question(savedQuestion)
            .answer(answerDto.getAnswer())
            .isCorrect(answerDto.getIsCorrect())
            .build());
      }
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleException(Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/{quizId}")
  public ResponseEntity<SessionId> createNewQuiz(@PathVariable(value = "quizId") Long quizId) {
    var username = securityContext.getLoggedInUser();
    var quiz = userRepository.findByName(username)
        .flatMap(user -> user.getQuizzes().stream()
            .filter(q -> q.getId().equals(quizId))
            .findFirst());

    if (quiz.isPresent()) {
      var sessionId = quizHandler.newQuiz(quiz.get());
      if (StringUtils.hasText(sessionId)) {
        return new ResponseEntity<>(new SessionId(sessionId), HttpStatus.CREATED);
      }
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
