package de.doubleslash.quiz.engine.processor;

import static de.doubleslash.quiz.engine.processor.QuizState.FINISHED;
import static de.doubleslash.quiz.engine.processor.QuizState.IDLE;
import static de.doubleslash.quiz.engine.processor.QuizState.RUNNING;

import de.doubleslash.quiz.engine.score.ScoreCalculator;
import de.doubleslash.quiz.engine.score.SimpleScoreCalculator;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.transport.dto.Participant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuizProcessor {

  private final Set<Question> questions;

  @Getter
  private final String sessionId;

  private final ScoreCalculator calc = new SimpleScoreCalculator();

  @Getter
  private final List<Participant> participants = new ArrayList<>();

  @Getter
  private QuizState state = IDLE;

  private Question currentQuestion;

  private Iterator<Question> qIterator;

  private OffsetDateTime timestampOfCurrentQuestion;

  private QuizSocket socket;

  private boolean waitingForAnswers;

  private Thread questionTimeThread = null;

  private int submittedAnswers;

  public boolean addParticipant(String nickname) {

    var alreadySet = participants.stream()
        .anyMatch(p -> p.getNickname().equals(nickname));

    if (alreadySet) {
      return false;
    }

    participants.add(new Participant(nickname));
    return true;
  }

  public void startQuiz(QuizSocket socket) {

    state = RUNNING;
    this.socket = socket;
    qIterator = questions.iterator();
  }

  public Optional<Question> getNextQuestion() {

    if (!waitingForAnswers) {
      if (qIterator.hasNext()) {
        var q = qIterator.next();
        qIterator.remove();
        timestampOfCurrentQuestion = OffsetDateTime.now();
        currentQuestion = q;
        submittedAnswers = 0;
        startQuestionTimeThread(q.getAnswerTime());
        return Optional.of(q);
      }
      socket.sendTimeOut(sessionId);
      return Optional.empty();
    }
    return Optional.of(currentQuestion);
  }

  public void showResults() {
    sendResults(!qIterator.hasNext());
  }

  private void startQuestionTimeThread(Long answerTime) {

    waitingForAnswers = true;
    questionTimeThread = new Thread(() -> {
      try {
        Thread.sleep(answerTime * 1000);
      } catch (InterruptedException v) {
        Thread.currentThread().interrupt();
      } finally {
        socket.sendTimeOut(sessionId);
      }
    });

    questionTimeThread.start();
  }

  private void sendResults(boolean isFinished) {

    waitingForAnswers = false;
    if (isFinished) {
      state = FINISHED;
    }

    socket.sendResults(sessionId, participants, isFinished);
  }

  public void addParticipantAnswer(String nickname, List<Long> answerIds) {

    checkSubmittedAnswers(nickname);
    socket.sendAnsweredParticipants(sessionId, submittedAnswers);

    if (waitingForAnswers) {
      participants.stream()
          .filter(p -> p.getNickname().equals(nickname))
          .findFirst()
          .ifPresent(p -> p.addScore(calculateScore(answerIds)));
    }
  }

  private void checkSubmittedAnswers(String nickname) {
    boolean isPresent = participants.stream().anyMatch(p -> p.getNickname().equals(nickname));
    if(!isPresent)
      return;

    submittedAnswers++;

    if(submittedAnswers == participants.size())
      questionTimeThread.interrupt();
  }

  private int calculateScore(List<Long> answerIds) {
    var offset = ChronoUnit.SECONDS.between(timestampOfCurrentQuestion, OffsetDateTime.now());
    var correctAnswers = currentQuestion.countCorrectAnswers(answerIds);
    var wrongAnswers = answerIds.size() - correctAnswers;
    return calc.calculateScore(offset, currentQuestion.getAnswerTime(), correctAnswers, wrongAnswers);
  }
}
