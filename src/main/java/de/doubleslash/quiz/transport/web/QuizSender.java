package de.doubleslash.quiz.transport.web;

import de.doubleslash.quiz.engine.processor.QuizState;
import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.transport.dto.Participant;
import de.doubleslash.quiz.transport.web.events.NewQuestionEvent;
import de.doubleslash.quiz.transport.web.events.ParticipantsUpdatedEvent;
import de.doubleslash.quiz.transport.web.events.QuizEvent;
import de.doubleslash.quiz.transport.web.events.QuizStateUpdatedEvent;
import de.doubleslash.quiz.transport.web.events.ResultsUpdatedEvent;
import de.doubleslash.quiz.transport.web.events.TimeOverEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuizSender {

  private final SimpMessagingTemplate simpMessagingTemplate;

  public void sendNewQuestionEvent(String sessionId, Question question) {
    sendEvent(sessionId, new NewQuestionEvent(question));
  }

  public void sendParticipantsUpdatedEvent(String sessionId, List<Participant> participants) {
    sendEvent(sessionId, new ParticipantsUpdatedEvent(participants));
  }

  public void sendResultsUpdatedEvent(String sessionId, List<Participant> participants,
      boolean isFinished) {
    sendEvent(sessionId, new ResultsUpdatedEvent(participants, isFinished));
  }

  public void sendQuizStateUpdatedEvent(String sessionId, QuizState state) {
    sendEvent(sessionId, new QuizStateUpdatedEvent(state));
  }

  public void sendTimeOverEvent(String sessionId) {
    sendEvent(sessionId, new TimeOverEvent());
  }

  private void sendEvent(String sessionId, QuizEvent event) {
    simpMessagingTemplate.convertAndSend("/sessions/" + sessionId, event);
  }
}
