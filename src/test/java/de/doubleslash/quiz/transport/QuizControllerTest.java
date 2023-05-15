package de.doubleslash.quiz.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import de.doubleslash.quiz.QuizApplication;
import de.doubleslash.quiz.repository.AnswerRepository;
import de.doubleslash.quiz.repository.QuestionRepository;
import de.doubleslash.quiz.repository.QuizRepository;
import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.repository.dao.auth.User;
import de.doubleslash.quiz.repository.dao.quiz.Answer;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import de.doubleslash.quiz.transport.controller.QuizController;
import de.doubleslash.quiz.transport.dto.AnswerDto;
import de.doubleslash.quiz.transport.dto.QuestionDto;
import de.doubleslash.quiz.transport.dto.QuizDto;
import de.doubleslash.quiz.transport.dto.QuizView;
import de.doubleslash.quiz.transport.security.SecurityContextService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@ActiveProfiles("hsqldb")
@SpringBootTest(classes = {
    QuizApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class QuizControllerTest {

  @Autowired
  private QuizController quizController;

  @Autowired
  private QuizRepository quizRepository;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private AnswerRepository answerRepository;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private SecurityContextService securityContext;

  Long QUIZ_ID;
  Long QUESTION_ID;
  Long SECOND_QUESTION_ID;
  Long ANSWER_ID;
  Long SECOND_ANSWER_ID;

  final String DEMO_USER = "DEMO";

  @BeforeAll
  public static void beforeAll() {
    System.setProperty("spring.datasource.url", "jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1");
  }

  @BeforeEach
  public void setupDatabase() {

    User newUser = User.builder()
        .name(DEMO_USER)
        .password(DEMO_USER)
        .enabled(true)
        .build();
    userRepository.save(newUser);

    var quiz = new Quiz();
    quiz.setName("Test Quiz");
    quiz.setUser(newUser);
    QUIZ_ID = quizRepository.save(quiz).getId();

    var question = new Question();
    question.setQuestion("Test Question");
    question.setAnswerTime(120L);
    question.setQuiz(quiz);
    QUESTION_ID = questionRepository.save(question).getId();

    var a1 = new Answer();
    a1.setAnswer("Test Answer");
    a1.setIsCorrect(true);
    a1.setQuestion(question);
    ANSWER_ID = answerRepository.save(a1).getId();

    var a2 = new Answer();
    a2.setAnswer("Test Answer 2");
    a2.setIsCorrect(false);
    a2.setQuestion(question);
    SECOND_ANSWER_ID = answerRepository.save(a2).getId();

    checkDatabase();
  }

  @AfterEach
  public void deleteEverythingInDatabase() {
    answerRepository.deleteAll();
    questionRepository.deleteAll();
    quizRepository.deleteAll();
    userRepository.deleteAll();
  }

  private void checkDatabase() {
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    var user = userRepository.findByName(DEMO_USER);
    var quiz = quizRepository.findById(QUIZ_ID);
    var question = questionRepository.findById(QUESTION_ID);
    var answer = answerRepository.findById(ANSWER_ID);
    var second_answer = answerRepository.findById(SECOND_ANSWER_ID);

    assertTrue(user.isPresent());
    assertTrue(quiz.isPresent());
    assertTrue(question.isPresent());
    assertTrue(answer.isPresent());
    assertTrue(second_answer.isPresent());
  }

  @Test
  public void testQueryGetAllQuiz_ReturnAllQuizzesFromUser() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    // Act
    List<QuizView> quizViews = quizController.getAllQuiz();

    // Assert
    QuizView quizView = quizViews.get(0);
    Quiz quiz = quizRepository.findById(QUIZ_ID).orElseThrow();

    assertEquals(1, quizViews.size());
    assertEquals(quiz.getId(), quizView.getId());
    assertEquals(quiz.getName(), quizView.getName());
  }

  @Test
  public void testQueryGetAllQuiz_FalseUser() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn("NOT_DEMO_USER");

    // Act
    List<QuizView> quizViews = quizController.getAllQuiz();

    // Assert
    assertEquals(0, quizViews.size());
  }


  @Test
  public void testQueryGetQuiz_ReturnCompleteQuiz() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    // Act
    QuizDto quizdto = quizController.getCompleteQuiz(QUIZ_ID);

    // Assert
    Quiz quiz = quizRepository.findById(QUIZ_ID).orElseThrow();
    Question question = quiz.getQuestions().stream().findFirst().orElseThrow();

    QuestionDto questionDto = quizdto.getQuestions().get(0);

    assertEquals(quizdto.getName(), quiz.getName());
    assertEquals(quizdto.getQuestions().size(), quiz.getQuestions().size());
    assertEquals(question.getQuestion(), questionDto.getQuestion());
    assertEquals(question.getAnswers().size(), questionDto.getAnswers().size());
  }

  @Test
  public void testQueryGetQuiz_InvalidUser() {
    when(securityContext.getLoggedInUser()).thenReturn("NOT_DEMO");

    assertNull(quizController.getCompleteQuiz(1L));
  }

  @Test
  public void testQueryGetQuiz_InvalidQuiz() {
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    assertNull(quizController.getCompleteQuiz(QUIZ_ID + 5));
  }

  @Test
  public void testQuerySaveNewQuiz_SaveNewQuiz() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String quizName = "New Test Quiz Name";
    String questionName = "New Test Question Name";
    String answerName = "New Test Answer Name";

    AnswerDto answerDto = AnswerDto.builder()
        .answer(answerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .question(questionName)
        .answers(List.of(answerDto))
        .answerTime(10L)
        .build();

    QuizDto quizDto = QuizDto.builder()
        .name(quizName)
        .questions(List.of(questionDto))
        .build();

    // Act
    ResponseEntity<Object> response = quizController.saveNewQuiz(quizDto);

    // Assert
    User user = userRepository.findByName(DEMO_USER).orElseThrow();
    Quiz quiz = user.getQuizzes().stream()
        .filter(q -> q.getName().equals(quizName))
        .findFirst()
        .orElseThrow();

    Question question = quiz.getQuestions().stream().findFirst().orElseThrow();
    Answer answer = question.getAnswers().stream().findFirst().orElseThrow();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(quizName, quiz.getName());
    assertEquals(questionName, question.getQuestion());
    assertEquals(answerName, answer.getAnswer());
  }

  @Test
  public void testQuerySaveNewQuiz_InvalidUser() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn("NOT " + DEMO_USER);

    // Act
    ResponseEntity<Object> response = quizController.saveNewQuiz(null);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void testQuerySaveNewQuiz_InvalidQuizDto() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    // Act
    ResponseEntity<Object> response = quizController.saveNewQuiz(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }


  @Test
  public void testQueryUpdateNewQuiz_UpdateQuizNameWithNoQuestions() {

    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String newQuizName = "New Quiz Name";

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name(newQuizName)
        .questions(null)
        .build();

    // Act
    quizController.updateQuiz(quizDto);

    // Assert
    Quiz quiz = quizRepository.findById(QUIZ_ID).orElseThrow();
    assertEquals(newQuizName, quiz.getName());
  }

  @Test
  public void testQueryUpdateNewQuiz_UpdateQuiz() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String newQuizName = "New Quiz Name";
    String newQuestionName = "New Question Name";
    String newAnswerName = "New Answer Name";
    String newSecondAnswerName = "New Second Answer";

    AnswerDto answerDto = AnswerDto.builder()
        .id(ANSWER_ID)
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    AnswerDto secondAnswerDto = AnswerDto.builder()
        .id(SECOND_ANSWER_ID)
        .answer(newSecondAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .id(QUESTION_ID)
        .question(newQuestionName)
        .answerTime(1020L)
        .answers(List.of(answerDto, secondAnswerDto))
        .build();

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name(newQuizName)
        .questions(List.of(questionDto))
        .build();

    // Act
    quizController.updateQuiz(quizDto);

    // Assert
    Optional<Quiz> quiz = quizRepository.findById(QUIZ_ID);
    Optional<Question> question = questionRepository.findById(QUESTION_ID);
    Optional<Answer> answer = answerRepository.findById(ANSWER_ID);
    Optional<Answer> secondAnswer = answerRepository.findById(SECOND_ANSWER_ID);

    assertTrue(quiz.isPresent());
    assertTrue(question.isPresent());
    assertTrue(answer.isPresent());
    assertTrue(secondAnswer.isPresent());

    assertEquals(newQuizName, quiz.get().getName());
    assertEquals(newQuestionName, question.get().getQuestion());
    assertEquals(newAnswerName, answer.get().getAnswer());
    assertEquals(newSecondAnswerName, secondAnswer.get().getAnswer());
  }

  @Test
  public void testQueryUpdateNewQuiz_InvalidQuizID() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID + 5)
        .name("Invalid Quiz")
        .questions(null)
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void testQueryUpdateNewQuiz_UserDoesntHaveQuiz() {
    // Arrange
    String notDemoUser = "NOT" + DEMO_USER;
    User user = User.builder()
        .name(notDemoUser)
        .password("secure_password")
        .enabled(true)
        .build();
    userRepository.save(user);

    when(securityContext.getLoggedInUser()).thenReturn(notDemoUser);

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name("Valid Quiz")
        .questions(null)
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void testQueryUpdateQuiz_deleteQuestion() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    createQuizWithTwoQuestions();

    String newQuizName = "New Quiz Name";
    String newQuestionName = "New Question Name";
    String newAnswerName = "New Answer Name";

    AnswerDto answerDto = AnswerDto.builder()
        .id(ANSWER_ID)
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .id(QUESTION_ID)
        .question(newQuestionName)
        .answerTime(1020L)
        .answers(List.of(answerDto))
        .build();

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name(newQuizName)
        .questions(List.of(questionDto))
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    var secondQuestion = questionRepository.findById(SECOND_QUESTION_ID);
    var secondAnswer = answerRepository.findById(SECOND_ANSWER_ID);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(secondQuestion.isEmpty());
    assertTrue(secondAnswer.isEmpty());
  }

  private void createQuizWithTwoQuestions() {
    deleteEverythingInDatabase();

    User newUser = User.builder()
        .name(DEMO_USER)
        .password(DEMO_USER)
        .enabled(true)
        .build();
    userRepository.save(newUser);

    var quiz = new Quiz();
    quiz.setName("Test Quiz");
    quiz.setUser(newUser);
    QUIZ_ID = quizRepository.save(quiz).getId();

    var question = new Question();
    question.setQuestion("Test Question");
    question.setAnswerTime(120L);
    question.setQuiz(quiz);
    QUESTION_ID = questionRepository.save(question).getId();

    var secondQuestion = new Question();
    secondQuestion.setQuestion("Second Test Question");
    secondQuestion.setAnswerTime(560L);
    secondQuestion.setQuiz(quiz);
    SECOND_QUESTION_ID = questionRepository.save(secondQuestion).getId();

    var a1 = new Answer();
    a1.setAnswer("First Question Test Answer");
    a1.setIsCorrect(true);
    a1.setQuestion(question);
    ANSWER_ID = answerRepository.save(a1).getId();

    var a2 = new Answer();
    a2.setAnswer("Second Question Test Answer 2");
    a2.setIsCorrect(false);
    a2.setQuestion(secondQuestion);
    SECOND_ANSWER_ID = answerRepository.save(a2).getId();
  }

  @Test
  public void testQueryUpdateQuiz_deleteAnswer() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String newQuizName = "New Quiz Name";
    String newQuestionName = "New Question Name";
    String newAnswerName = "New Answer Name";

    AnswerDto answerDto = AnswerDto.builder()
        .id(ANSWER_ID)
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .id(QUESTION_ID)
        .question(newQuestionName)
        .answerTime(1020L)
        .answers(List.of(answerDto))
        .build();

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name(newQuizName)
        .questions(List.of(questionDto))
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    var answer = answerRepository.findById(ANSWER_ID);
    var secondAnswer = answerRepository.findById(SECOND_ANSWER_ID);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(answer.isPresent());
    assertTrue(secondAnswer.isEmpty());
  }

  @Test
  public void testQueryUpdateQuiz_createQuestion() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String newQuestionName = "Newly created Question Name";
    String newAnswerName = "Newly created Answer Name";

    AnswerDto answerDto = AnswerDto.builder()
        .id(ANSWER_ID)
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .id(QUESTION_ID)
        .question(newQuestionName)
        .answerTime(1020L)
        .answers(List.of(answerDto))
        .build();

    AnswerDto newAnswerDto = AnswerDto.builder()
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto newQuestionDto = QuestionDto.builder()
        .question(newQuestionName)
        .answerTime(500L)
        .answers(List.of(newAnswerDto))
        .build();

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name("Updated Quiz Name")
        .questions(List.of(questionDto, newQuestionDto))
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    Optional<Quiz> quiz = quizRepository.findById(QUIZ_ID);
    assertTrue(quiz.isPresent());

    Optional<Question> newQuestion = quiz.get().getQuestions().stream()
        .filter(question -> question.getQuestion().equals(newQuestionName))
        .findFirst();
    assertTrue(newQuestion.isPresent());

    Optional<Answer> newAnswer = newQuestion.get().getAnswers().stream().findFirst();
    assertTrue(newAnswer.isPresent());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2, quiz.get().getQuestions().size());
    assertEquals(newQuestionName, newQuestion.get().getQuestion());
    assertEquals(1, newQuestion.get().getAnswers().size());
    assertEquals(newAnswerName, newAnswer.get().getAnswer());
  }

  @Test
  public void testQueryUpdateQuiz_createAnswer() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    String newQuestionName = "Updated Question";
    String newAnswerName = "Newly created Answer Name";

    AnswerDto newAnswerDto = AnswerDto.builder()
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    AnswerDto answerDto = AnswerDto.builder()
        .id(ANSWER_ID)
        .answer(newAnswerName)
        .isCorrect(true)
        .build();

    QuestionDto questionDto = QuestionDto.builder()
        .id(QUESTION_ID)
        .question(newQuestionName)
        .answerTime(1020L)
        .answers(List.of(answerDto, newAnswerDto))
        .build();

    QuizDto quizDto = QuizDto.builder()
        .id(QUIZ_ID)
        .name("Updated Quiz Name")
        .questions(List.of(questionDto))
        .build();

    // Act
    var response = quizController.updateQuiz(quizDto);

    // Assert
    Optional<Quiz> quiz = quizRepository.findById(QUIZ_ID);
    assertTrue(quiz.isPresent());

    Optional<Question> question = quiz.get().getQuestions().stream().findFirst();
    assertTrue(question.isPresent());

    Optional<Answer> newAnswer = question.get().getAnswers().stream()
        .filter(answer -> answer.getAnswer().equals(newAnswerName))
        .findFirst();
    assertTrue(newAnswer.isPresent());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2, question.get().getAnswers().size());
    assertEquals(newAnswerName, newAnswer.get().getAnswer());
  }

  @Test
  public void testQueryDeleteQuiz_deleteQuiz() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    // Act
    var response = quizController.removeQuiz(QUIZ_ID);

    // Assert
    var quizList = quizRepository.findAll();
    var questionList = questionRepository.findAll();
    var answerList = answerRepository.findAll();

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(0, quizList.size());
    assertEquals(0, questionList.size());
    assertEquals(0, answerList.size());
  }

  @Test
  void testQueryDeleteQuiz_InvalidUser() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn("NOT" + DEMO_USER);

    // Act
    var response = quizController.removeQuiz(QUIZ_ID);

    // Assert
    var quiz = quizRepository.findById(QUIZ_ID);
    var question = questionRepository.findById(QUESTION_ID);
    var answer = answerRepository.findById(ANSWER_ID);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(quiz.isPresent());
    assertTrue(question.isPresent());
    assertTrue(answer.isPresent());
  }

  @Test
  void testQueryDeleteQuiz_InvalidQuiz() {
    // Arrange
    when(securityContext.getLoggedInUser()).thenReturn(DEMO_USER);

    // Act
    var response = quizController.removeQuiz(QUIZ_ID + 5);

    // Assert
    var quiz = quizRepository.findById(QUIZ_ID);
    var question = questionRepository.findById(QUESTION_ID);
    var answer = answerRepository.findById(ANSWER_ID);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertTrue(quiz.isPresent());
    assertTrue(question.isPresent());
    assertTrue(answer.isPresent());
  }

  @Test
  void testQueryDeleteQuiz_UserDoesntHaveQuiz() {
    // Arrange
    String notDemoUser = "NOT" + DEMO_USER;
    User user = User.builder()
        .name(notDemoUser)
        .password("secure_password")
        .enabled(true)
        .build();
    userRepository.save(user);

    when(securityContext.getLoggedInUser()).thenReturn(notDemoUser);

    // Act
    var response = quizController.removeQuiz(QUIZ_ID);

    // Assert
    var quiz = quizRepository.findById(QUIZ_ID);
    var question = questionRepository.findById(QUESTION_ID);
    var answer = answerRepository.findById(ANSWER_ID);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(quiz.isPresent());
    assertTrue(question.isPresent());
    assertTrue(answer.isPresent());
  }
}
