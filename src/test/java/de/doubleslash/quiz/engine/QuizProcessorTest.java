package de.doubleslash.quiz.engine;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Lists;
import de.doubleslash.quiz.engine.processor.QuizProcessor;
import de.doubleslash.quiz.engine.processor.QuizSocket;
import de.doubleslash.quiz.engine.processor.QuizState;
import de.doubleslash.quiz.repository.dao.quiz.Answer;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.transport.dto.Participant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class QuizProcessorTest {

  private final String SESSION_ID = "Test Session";

  private final String TEST_PARTICIPANT = "Test Participant";

  private final Set<Question> questions = new HashSet<>();

  @Mock
  private QuizSocket quizSocket;

  @Captor
  private ArgumentCaptor<ArrayList<Participant>> participantCaptor;

  @BeforeEach
  public void init(){
    MockitoAnnotations.openMocks(this);

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
    question.setAnswerTime(1L);
    question.setAnswers(answers);
    questions.add(question);
  }

  @Test
  void testStartQuiz_StateRunning(){
    //Arrange
    var processor = new QuizProcessor(questions, SESSION_ID);
    processor.addParticipant(TEST_PARTICIPANT);

    //Act
    processor.startQuiz(quizSocket);

    //Assert
    assertEquals(QuizState.RUNNING, processor.getState());
  }

  @Test
  void testStartQuizRun_StateFinishedAfterQuestions() {
    //Arrange
    var processor = new QuizProcessor(questions, SESSION_ID);
    processor.addParticipant(TEST_PARTICIPANT);

    //Act
    var participants = processor.getParticipants();
    processor.startQuiz(quizSocket);
    processor.getNextQuestion();
    processor.showResults();

    await()
        .atMost(2, TimeUnit.SECONDS)
        .until(() -> processor.getState().equals(QuizState.FINISHED));

    //Assert
    assertEquals(QuizState.FINISHED, processor.getState());
    assertEquals(1, participants.size());
    verify(quizSocket, times(1)).sendResults(SESSION_ID, participants, true);
  }

  @Test
  void testStartQuizRun_CorrectlyCalculatedScore() {
    //Arrange
    var processor = new QuizProcessor(questions, SESSION_ID);
    processor.addParticipant(TEST_PARTICIPANT);


    //Act
    var participants = processor.getParticipants();
    processor.startQuiz(quizSocket);
    processor.getNextQuestion();
    processor.addParticipantAnswer(TEST_PARTICIPANT, Lists.newArrayList(1L));
    processor.showResults();

    await()
        .atMost(2, TimeUnit.SECONDS)
        .until(() -> processor.getState().equals(QuizState.FINISHED));

    //Assert
    assertEquals(QuizState.FINISHED, processor.getState());
    assertEquals(1, participants.size());
    verify(quizSocket, times(1)).sendResults(eq(SESSION_ID), participantCaptor.capture(), eq(true));
    assertEquals(1, participantCaptor.getValue().get(0).getScore());
  }
}
