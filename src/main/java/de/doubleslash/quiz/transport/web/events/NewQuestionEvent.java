package de.doubleslash.quiz.transport.web.events;

import de.doubleslash.quiz.repository.dao.quiz.Question;
import de.doubleslash.quiz.transport.dto.QuestionView;
import lombok.Getter;

@Getter
public class NewQuestionEvent implements QuizEvent {

  private final QuestionView question;
  private final int amountOfParticipants;

  public NewQuestionEvent(Question question, int amountOfParticipants) {
    this.question = new QuestionView(question);
    this.amountOfParticipants = amountOfParticipants;
  }
}
