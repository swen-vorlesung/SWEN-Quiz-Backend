package de.doubleslash.quiz.engine.web;

import java.util.ArrayList;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QuizReceiver {

  private final List<QuizObserver> observers = new ArrayList<>();

  public void register(QuizObserver observer) {
    observers.add(observer);
  }

  @MessageMapping("/order")
  public void greeting() {

  }
}
