package de.doubleslash.quiz.transport.web.events;

public interface QuizEvent {

  default String getEventName() {
    return this.getClass().getSimpleName();
  }
}
