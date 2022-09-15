package de.doubleslash.quiz.engine;

import static de.doubleslash.quiz.engine.processor.QuizState.IDLE;
import static de.doubleslash.quiz.engine.processor.QuizState.RUNNING;

import de.doubleslash.quiz.engine.processor.QuizProcessor;
import de.doubleslash.quiz.engine.processor.QuizSocket;
import de.doubleslash.quiz.engine.processor.QuizState;
import de.doubleslash.quiz.engine.repository.QuizRepository;
import de.doubleslash.quiz.engine.web.QuizObserver;
import de.doubleslash.quiz.engine.web.QuizReceiver;
import de.doubleslash.quiz.engine.web.QuizSender;
import de.doubleslash.quiz.engine.web.dto.Participant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizProxy implements QuizObserver, QuizSocket {

  private final QuizReceiver receiver;

  private final QuizSender sender;

  private final QuizRepository repo;

  private final List<QuizProcessor> quizes = new ArrayList<>();

  @PostConstruct
  public void register() {
    receiver.register(this);
  }

  @Override
  public boolean newQuiz(Long quizId, String sessionId) {
    return repo.findById(quizId)
        .map(q -> quizes.add(new QuizProcessor(q.getQuestions(), sessionId)))
        .orElse(false);
  }

  @Override
  public boolean addParticipant(String sessionId, String nickName) {
    return findIdleQuizProcessor(sessionId)
        .map(processor -> processor.addParticipant(nickName))
        .orElse(false);
  }

  @Override
  public boolean startQuiz(String sessionId) {
    return findIdleQuizProcessor(sessionId)
        .map(q -> {
          q.startQuiz(this);
          return true;
        })
        .orElse(false);
  }

  @Override
  public boolean addParticipantInput(String sessionId, String nickname, Long answerId) {
    return findRunningQuizProcessor(sessionId)
        .map(q -> {
          q.addParticipantAnswer(nickname, answerId);
          return true;
        })
        .orElse(false);
  }

  @Override
  public boolean startNewQuestion(String sessionId) {
    return findRunningQuizProcessor(sessionId)
        .map(QuizProcessor::getNextQuestion)
        .flatMap(q -> q)
        .map(q -> {
          sender.sendNewQuestionToQuiz(sessionId, q);
          return true;
        })
        .orElse(false);
  }

  @Override
  public void sendResults(List<Participant> participants, boolean isFinished) {
    sender.sendResultsToQuiz(participants, isFinished);
  }

  private Optional<QuizProcessor> findIdleQuizProcessor(String sessionId) {
    return findQuizProcessorByState(sessionId, IDLE);
  }

  private Optional<QuizProcessor> findRunningQuizProcessor(String sessionId) {
    return findQuizProcessorByState(sessionId, RUNNING);
  }

  private Optional<QuizProcessor> findQuizProcessorByState(String sessionId, QuizState state) {
    return quizes.stream()
        .filter(q -> q.getSessionId().equals(sessionId))
        .filter(q -> q.getState().equals(state))
        .findFirst();
  }
}
