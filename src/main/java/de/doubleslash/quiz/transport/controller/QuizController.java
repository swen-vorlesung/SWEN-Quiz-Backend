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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/{quizId}")
  public QuizDto GetCompleteQuiz(@PathVariable(value = "quizId") Long quizId) {
    // TODO: Check if User exists and has this quiz

    var username = securityContext.getLoggedInUser();

    // TODO: Split user and getting the quiz
    Optional<Quiz> quizOptional = userRepository.findByName(username)
        .map(user -> user.getQuizzes().stream()
            .filter(quiz -> quiz.getId().equals(quizId))
            .findFirst())
        .get();

    if (quizOptional.isEmpty()) {
      return null;
    }

    Quiz quiz = quizOptional.get();

    return QuizDto.builder()
        .id(quiz.getId())
        .name(quiz.getName())
        .questions(quiz.getQuestions().stream()
            .map(question -> QuestionDto.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answerTime(question.getAnswerTime())
                .answers(question.getAnswers().stream()
                    .map(answer -> AnswerDto.builder()
                        .id(answer.getId())
                        .answer(answer.getAnswer())
                        .isCorrect(answer.getIsCorrect())
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList()))
        .build();
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
      saveQuestionAndAnswers(questionDto, savedQuiz);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleException(Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @PutMapping
  public ResponseEntity<Object> updateQuiz(@RequestBody QuizDto updateQuiz) {
    var username = securityContext.getLoggedInUser();
    var user = userRepository.findByName(username);

    Optional<Quiz> optionalQuiz = quizRepository.findById(updateQuiz.getId());
    if (optionalQuiz.isEmpty() || user.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Quiz oldQuiz = optionalQuiz.get();
    if (!oldQuiz.getUser().getId().equals(user.get().getId())) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    var updatedQuestions = updateQuiz.getQuestions();
    var oldQuestions = oldQuiz.getQuestions();

    oldQuiz.setName(updateQuiz.getName());

    removeOldQuestions(oldQuestions, updatedQuestions);
    createOrUpdateQuestions(oldQuiz, updatedQuestions);

    // Update Answers
    for (var updatedQuestion : updateQuiz.getQuestions()) {
      if (updatedQuestion.getId() == null) {
        continue;
      }

      var oldQuestion = oldQuiz.getQuestions().stream()
          .filter(question -> question.getId().equals(updatedQuestion.getId()))
          .findFirst();

      if (oldQuestion.isEmpty()) {
        continue;
      }

      removeOldAnswers(oldQuestion.get().getAnswers(), updatedQuestion.getAnswers());
      createOrUpdateAnswers(oldQuestion.get(), updatedQuestion.getAnswers());
    }

    quizRepository.save(oldQuiz);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  private void createOrUpdateQuestions(Quiz oldQuiz, List<QuestionDto> updatedQuestions) {
    var oldQuestions = oldQuiz.getQuestions();
    for (var updatedQuestion : updatedQuestions) {
      if (updatedQuestion.getId() != null) {
        // Update Question

        oldQuestions.stream()
            .filter(oldQuestion -> oldQuestion.getId().equals(updatedQuestion.getId()))
            .findFirst()
            .ifPresent(oldQuestion -> {
              oldQuestion.setQuestion(updatedQuestion.getQuestion());
              oldQuestion.setAnswerTime(updatedQuestion.getAnswerTime());
            });
      } else {
        // Creating new Question with new answers
        saveQuestionAndAnswers(updatedQuestion, oldQuiz);
      }
    }
  }

  private void createOrUpdateAnswers(Question oldQuestion, List<AnswerDto> updatedAnswers) {
    var oldAnswers = oldQuestion.getAnswers();
    for (var updatedAnswer : updatedAnswers) {
      if (updatedAnswer.getId() != null) {
        oldAnswers.stream().filter(oldAnswer -> oldAnswer.getId().equals(updatedAnswer.getId()))
            .findFirst()
            .ifPresent(oldAnswer -> {
              oldAnswer.setAnswer(updatedAnswer.getAnswer());
              oldAnswer.setIsCorrect(updatedAnswer.getIsCorrect());
            });
      } else {
        saveAnswer(updatedAnswer, oldQuestion);
      }
    }
  }

  private void removeOldQuestions(Set<Question> oldQuestions, List<QuestionDto> updatedQuestion) {
    // Remove old questions
    Set<Question> toRemoveQuestions = new HashSet<>();
    for (var oldQuestion : oldQuestions) {
      if (updatedQuestion.contains(oldQuestion)) {
        continue;
      }

      for (var answer : oldQuestion.getAnswers()) {
        answerRepository.deleteById(answer.getId());
      }

      questionRepository.deleteById(oldQuestion.getId());
      toRemoveQuestions.add(oldQuestion);
    }
    oldQuestions.removeAll(toRemoveQuestions);
  }

  private void removeOldAnswers(Set<Answer> oldAnswers, List<AnswerDto> updatedAnswers) {
    // Remove old answers
    Set<Answer> toRemoveAnswer = new HashSet<>();
    for (var oldAnswer : oldAnswers) {
      if (updatedAnswers.contains(oldAnswer)) {
        continue;
      }

      log.info("Removing Answer: " + oldAnswer.getAnswer());
      toRemoveAnswer.add(oldAnswer);
      answerRepository.deleteById(oldAnswer.getId());
    }
    oldAnswers.removeAll(toRemoveAnswer);
  }

  private void saveQuestionAndAnswers(QuestionDto newQuestion, Quiz quiz) {
    var savedQuestion = questionRepository.save(Question.builder()
        .quiz(quiz)
        .question(newQuestion.getQuestion())
        .answerTime(newQuestion.getAnswerTime())
        .build());

    // Save answers
    for (AnswerDto answerDto : newQuestion.getAnswers()) {
      saveAnswer(answerDto, savedQuestion);
    }
  }

  private void saveAnswer(AnswerDto newAnswer, Question question) {
    answerRepository.save(Answer.builder()
        .question(question)
        .answer(newAnswer.getAnswer())
        .isCorrect(newAnswer.getIsCorrect())
        .build());
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
