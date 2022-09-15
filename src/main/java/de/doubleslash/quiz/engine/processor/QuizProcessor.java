package de.doubleslash.quiz.engine.processor;

import static de.doubleslash.quiz.engine.processor.QuizState.FINISHED;
import static de.doubleslash.quiz.engine.processor.QuizState.IDLE;
import static de.doubleslash.quiz.engine.processor.QuizState.RUNNING;

import de.doubleslash.quiz.engine.repository.dao.Question;
import de.doubleslash.quiz.engine.score.ScoreCalculator;
import de.doubleslash.quiz.engine.score.SimpleScoreCalculator;
import de.doubleslash.quiz.engine.web.dto.Participant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuizProcessor {

  private final List<Question> questions;

  @Getter
  private final String sessionId;

  private final ScoreCalculator calc = new SimpleScoreCalculator();

  private final List<Participant> participants = new ArrayList<>();

  @Getter
  private QuizState state = IDLE;

  private Question currentQuestion;

  private Iterator<Question> qIterator;

  private OffsetDateTime timestampOfCurrentQuestion;

  private QuizSocket socket;

  private boolean waitingForAnswers;

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
        startQuestionTimeThread(q.getAnswerTime());
        return Optional.of(q);
      }
      state = FINISHED;
      sendResults(true);
      return Optional.empty();
    }
    return Optional.of(currentQuestion);
  }

  private void startQuestionTimeThread(Long answerTime) {
    waitingForAnswers = true;
    new Thread(() -> {
      try {
        Thread.sleep(answerTime * 1000);
        sendResults(false);
      } catch (InterruptedException v) {
        Thread.currentThread().interrupt();
      }
    }).start();
  }

  private void sendResults(boolean isFinished) {
    waitingForAnswers = false;
    socket.sendResults(participants, isFinished);
  }

  public void addParticipantAnswer(String nickname, Long answerId) {
    if (waitingForAnswers) {
      participants.stream()
          .filter(p -> p.getNickname().equals(nickname))
          .findFirst()
          .ifPresent(p -> p.addScore(calculateScore(answerId)));
    }
  }

  private int calculateScore(Long answerId) {
    var offset = ChronoUnit.SECONDS.between(timestampOfCurrentQuestion, OffsetDateTime.now());
    return calc.calculateScore(offset, currentQuestion.isCorrectAnswer(answerId));
  }
}
