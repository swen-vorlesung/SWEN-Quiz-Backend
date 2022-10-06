package de.doubleslash.quiz.transport.web;

import de.doubleslash.quiz.transport.controller.QuizHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventHandler {

  private final QuizHandler quizHandler;

  @EventListener
  public void handleSessionConnected(SessionSubscribeEvent event) {
    var destination = (String) event.getMessage().getHeaders().get("simpDestination");
    if (destination != null) {
      var sessionId = destination.replace("/sessions/", "");
      quizHandler.notifyQuizWithAllParticipants(sessionId);
    }
  }

  @EventListener
  public void handleSessionUnsubscribed(SessionUnsubscribeEvent event) {
    log.info(event.toString());
  }

  @EventListener
  public void handleSessionDisconnect(SessionDisconnectEvent event) {
    log.info(event.toString());
  }
}
