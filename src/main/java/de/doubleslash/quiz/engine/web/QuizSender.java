package de.doubleslash.quiz.engine.web;

import de.doubleslash.quiz.engine.dto.Participant;
import de.doubleslash.quiz.engine.dto.Results;
import de.doubleslash.quiz.engine.repository.dao.Question;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Controller
@RequiredArgsConstructor
public class QuizSender {

  private final SimpMessagingTemplate simpMessagingTemplate;

  public void sendNewQuestionToQuiz(String sessionId, Question question) {
    simpMessagingTemplate.convertAndSend("/quiz/" + sessionId, question);
  }

  public void updateParticipantsToQuiz(String sessionId, List<Participant> participants) {
    simpMessagingTemplate.convertAndSend("/sessions/" + sessionId + "/waitingroom", participants);
  }

  public void sendResultsToQuiz(String sessionId, List<Participant> participants, boolean isFinished) {
    simpMessagingTemplate.convertAndSend("/results/" + sessionId,
        Results.builder()
            .participants(participants)
            .isFinished(isFinished)
            .build());
  }
}
