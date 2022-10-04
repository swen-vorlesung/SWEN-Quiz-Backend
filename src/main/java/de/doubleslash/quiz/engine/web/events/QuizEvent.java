package de.doubleslash.quiz.engine.web.events;

public interface QuizEvent {

  default String getEventName() {
    return this.getClass().getSimpleName();
  }
}
