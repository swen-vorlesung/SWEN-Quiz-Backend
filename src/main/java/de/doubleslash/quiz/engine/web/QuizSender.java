package de.doubleslash.quiz.engine.web;

import de.doubleslash.quiz.engine.repository.dao.Question;
import de.doubleslash.quiz.engine.web.dto.Participant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuizSender {

  private final SimpMessagingTemplate simpMessagingTemplate;

  public void sendNewQuestionToQuiz(String sessionId, Question question) {
  }

  public void sendResultsToQuiz(List<Participant> participants, boolean isFinished) {
  }
}
