package de.doubleslash.quiz.engine;

import static de.doubleslash.quiz.engine.processor.QuizState.FINISHED;
import static de.doubleslash.quiz.engine.processor.QuizState.IDLE;
import static de.doubleslash.quiz.engine.processor.QuizState.RUNNING;

import de.doubleslash.quiz.engine.processor.QuizProcessor;
import de.doubleslash.quiz.engine.processor.QuizSocket;
import de.doubleslash.quiz.engine.processor.QuizState;
import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import de.doubleslash.quiz.transport.controller.QuizHandler;
import de.doubleslash.quiz.transport.dto.AnswerView;
import de.doubleslash.quiz.transport.dto.Answers;
import de.doubleslash.quiz.transport.dto.Participant;
import de.doubleslash.quiz.transport.web.QuizSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizAdapter implements QuizHandler, QuizSocket {

  private final QuizSender sender;

  private final List<QuizProcessor> quizes = new ArrayList<>();

  @Override
  public String newQuiz(Quiz quiz) {

    var sessionId = RandomStringUtils.randomAlphanumeric(5);
    quizes.add(new QuizProcessor(quiz.getQuestions(), sessionId));
    return sessionId;
  }

  @Override
  public boolean addParticipant(String sessionId, String nickName) {
    return findIdleQuizProcessor(sessionId)
        .map(processor ->
        {
          processor.addParticipant(nickName);
          sender.sendParticipantsUpdatedEvent(sessionId, processor.getParticipants());
          return true;
        })
        .orElse(false);
  }

  @Override
  public void notifyQuizWithAllParticipants(String sessionId) {
    findIdleQuizProcessor(sessionId)
        .ifPresent(processor ->
            sender.sendParticipantsUpdatedEvent(sessionId, processor.getParticipants()));
  }


  @Override
  public boolean startQuiz(String sessionId) {
    return findIdleQuizProcessor(sessionId)
        .map(q -> {
          q.startQuiz(this);
          notifyQuizStarted(sessionId);
          startNewQuestion(sessionId);
          return true;
        })
        .orElse(false);
  }

  @Override
  public boolean addParticipantInput(String sessionId, String nickname, Answers answerId) {
    return findRunningQuizProcessor(sessionId)
        .map(q -> {
          q.addParticipantAnswer(nickname, answerId.getAnswers().stream()
              .map(AnswerView::getId)
              .collect(Collectors.toList()));
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
          sender.sendNewQuestionEvent(sessionId, q);
          return true;
        })
        .orElse(false);
  }

  @Override
  public void notifyQuizStarted(String sessionId) {
    sender.sendQuizStateUpdatedEvent(sessionId, RUNNING);
  }

  @Override
  public void sendResults(String sessionId, List<Participant> participants, boolean isFinished) {
    sender.sendResultsUpdatedEvent(sessionId, participants, isFinished);
    if (isFinished) {
      sender.sendQuizStateUpdatedEvent(sessionId, FINISHED);
      
      quizes
          .stream()
          .filter(q -> q.getSessionId().equals(sessionId))
          .findFirst()
          .ifPresent(quizes::remove);
    }
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
