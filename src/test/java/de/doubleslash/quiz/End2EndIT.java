package de.doubleslash.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import de.doubleslash.quiz.repository.QuizRepository;
import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.repository.dao.quiz.Answer;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import de.doubleslash.quiz.transport.controller.AuthenticationController;
import de.doubleslash.quiz.transport.controller.ParticipantController;
import de.doubleslash.quiz.transport.controller.QuizController;
import de.doubleslash.quiz.transport.controller.SessionController;
import de.doubleslash.quiz.transport.dto.AnswerView;
import de.doubleslash.quiz.transport.dto.Answers;
import de.doubleslash.quiz.transport.dto.LogIn;
import de.doubleslash.quiz.transport.dto.NickName;
import de.doubleslash.quiz.transport.dto.Participant;
import de.doubleslash.quiz.transport.security.SecurityContextService;
import de.doubleslash.quiz.transport.web.QuizSender;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

abstract class AbstractContainerTest {

  final static DockerImageName MY_IMAGE = DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest")
          .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");

  @Container
  public final static MSSQLServerContainer<?> DATABASE = new MSSQLServerContainer<>(MY_IMAGE);

  static {
    DATABASE.start();
    System.setProperty("spring.datasource.url", DATABASE.getJdbcUrl());
    System.setProperty("flyway.url", DATABASE.getJdbcUrl());
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { QuizApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Configuration
public class End2EndIT extends AbstractContainerTest {

  public static final String E_2_E_TEST_QUIZ = "E2ETestQuiz";

  @Autowired
  private AuthenticationController logInController;

  @Autowired
  private QuizController quizController;

  @Autowired
  private SessionController sessionController;

  @Autowired
  private ParticipantController participantController;

  @Autowired
  private UserRepository repo;

  @Autowired
  private QuizRepository qrepo;

  @MockBean
  private SecurityContextService securityContext;

  @MockBean
  private QuizSender socket;

  @Captor
  private ArgumentCaptor<ArrayList<Participant>> participantCaptor;

  @BeforeAll
  public void setUp() {
    var question = new Question();
    var answers = new HashSet<Answer>();
    var a1 = new Answer();
    a1.setId(1L);
    a1.setAnswer("Test 1");
    a1.setIsCorrect(true);

    var a2 = new Answer();
    a2.setId(2L);
    a2.setAnswer("Test 2");
    a2.setIsCorrect(false);

    answers.add(a1);
    answers.add(a2);

    question.setQuestion("Test");
    question.setAnswerTime(120L);
    question.setAnswers(answers);
    var questions = new ArrayList<Question>();
    questions.add(question);

    var quiz = new Quiz();
    quiz.setQuestions(questions);
    quiz.setName(E_2_E_TEST_QUIZ);
    quiz.setUser(repo.findByName("DEMO").get());

    qrepo.save(quiz);
  }

  @AfterAll
  public void close() {
    DATABASE.close();
  }

  @Test
  public void testLogIn_Successful() {
    //Arrange
    var login = new LogIn();
    login.setPassword("DEMO");
    login.setUsername("DEMO");

    //Act
    var token = logInController.login(login);

    //Assert
    assertNotNull(token);
  }

  @Test
  public void testLogIn_WrongCredentials_Unauthorized() {
    //Arrange
    var login = new LogIn();
    login.setPassword("DEMO");
    login.setUsername("DEMO1");
    RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {

      //Act
      var token = logInController.login(login);
    });

    //Assert
    assertTrue(runtimeException.getMessage().contains("invalid login and/or password"));
  }

  @Test
  public void testQueryQuizzesForUser_ReturnsAllQuizzesForUser() {
    //Arrange
    when(securityContext.getLoggedInUser()).thenReturn("DEMO");

    //Act
    var quizzes = quizController.getAllQuiz();

    //Assert
    assertEquals(2, quizzes.size());
  }

  @Test
  public void testQueryStartQuiz_RunsQuiz() {
    //Arrange
    when(securityContext.getLoggedInUser()).thenReturn("DEMO");

    //Act
    var quizzes = quizController.getAllQuiz();
    var sessionId = quizzes.stream().filter(q -> E_2_E_TEST_QUIZ.equals(q.getName())).findFirst().map(q -> {
      var session = quizController.createNewQuiz(q.getId());
      sessionController.startQuiz(session.getBody().getSessionId());
      return session;
    });

    //Assert
    assertEquals(2, quizzes.size());
    assertTrue(sessionId.isPresent());
    assertNotNull(sessionId.get().getBody());
    verify(socket, timeout(2000).atLeastOnce()).sendResultsUpdatedEvent(
            eq(sessionId.get().getBody().getSessionId()), participantCaptor.capture(), eq(true));
  }

  @Test
  @Disabled("Functionality isn't yet implemented")
  public void testQueryStartQuizWithParticipants_RunsQuiz() {
    //Arrange
    when(securityContext.getLoggedInUser()).thenReturn("DEMO");

    //Act
    var quizzes = quizController.getAllQuiz();
    var sessionId = quizzes.stream().filter(q -> E_2_E_TEST_QUIZ.equals(q.getName())).findFirst().map(q -> {
      var session = quizController.createNewQuiz(q.getId());
      var id = session.getBody().getSessionId();
      participantController.addParticipant(new NickName("TestParticipant"), id);
      sessionController.startQuiz(id);
      participantController.addAnswer(id, "TestParticipant",
          new Answers(Lists.newArrayList(new AnswerView(1L, "null", true))));
      return id;
    });

    //Assert
    assertEquals(2, quizzes.size());
    assertTrue(sessionId.isPresent());
    verify(socket, timeout(2000).atLeastOnce()).sendResultsUpdatedEvent(eq(sessionId.get()),
            participantCaptor.capture(), eq(true));
    assertEquals(1, participantCaptor.getValue().get(0).getScore());
  }
}
