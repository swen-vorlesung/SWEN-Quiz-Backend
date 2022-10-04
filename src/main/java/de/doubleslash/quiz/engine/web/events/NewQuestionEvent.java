package de.doubleslash.quiz.engine.web.events;

import de.doubleslash.quiz.engine.dto.QuestionView;
import de.doubleslash.quiz.engine.repository.dao.quiz.Question;
import lombok.Getter;

@Getter
public class NewQuestionEvent implements QuizEvent {

  private final QuestionView question;

  public NewQuestionEvent(Question question) {
    this.question = new QuestionView(question);
  }
}
