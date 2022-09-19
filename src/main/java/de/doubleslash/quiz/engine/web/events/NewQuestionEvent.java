package de.doubleslash.quiz.engine.web.events;

import de.doubleslash.quiz.engine.repository.dao.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewQuestionEvent implements QuizEvent {

  private Question question;
}
