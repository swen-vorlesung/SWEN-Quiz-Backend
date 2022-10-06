package de.doubleslash.quiz.transport.web.events;

import de.doubleslash.quiz.engine.processor.QuizState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizStateUpdatedEvent implements QuizEvent {

  private QuizState state;
}
