package de.doubleslash.quiz.transport.web.events;

import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.transport.dto.QuestionView;
import lombok.Getter;

@Getter
public class NewQuestionEvent implements QuizEvent {

  private final QuestionView question;

  public NewQuestionEvent(Question question) {
    this.question = new QuestionView(question);
  }
}
