package de.doubleslash.quiz.engine.web;

import de.doubleslash.quiz.engine.dto.Participant;
import de.doubleslash.quiz.engine.processor.QuizState;
import de.doubleslash.quiz.engine.repository.dao.Question;
import de.doubleslash.quiz.engine.web.events.NewQuestionEvent;
import de.doubleslash.quiz.engine.web.events.ParticipantsUpdatedEvent;
import de.doubleslash.quiz.engine.web.events.QuizEvent;
import de.doubleslash.quiz.engine.web.events.QuizStateUpdatedEvent;
import de.doubleslash.quiz.engine.web.events.ResultsUpdatedEvent;
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

  public void sendResultsUpdatedEvent(String sessionId, List<Participant> participants) {
    sendEvent(sessionId, new ResultsUpdatedEvent(participants));
  }

  public void sendQuizStateUpdatedEvent(String sessionId, QuizState state) {
    sendEvent(sessionId, new QuizStateUpdatedEvent(state));
  }

  private void sendEvent(String sessionId, QuizEvent event) {
    simpMessagingTemplate.convertAndSend("/sessions/" + sessionId, event);
  }
}
