package de.doubleslash.quiz.engine.web;

import de.doubleslash.quiz.engine.controller.QuizHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class StompEventHandler {

  private final QuizHandler quizHandler;

  @EventListener
  public void handleSessionConnected(SessionSubscribeEvent event) {
    var destination = (String) event.getMessage().getHeaders().get("simpDestination");
    if (destination != null && destination.contains("waitingroom")) {
      var sessionId = destination.replace("/sessions/", "").replace("/waitingroom", "");
      quizHandler.notifyQuizWithAllParticipants(sessionId);
    }
  }
}
