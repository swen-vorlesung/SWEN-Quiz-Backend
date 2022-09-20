package de.doubleslash.quiz.engine.dto;

import de.doubleslash.quiz.engine.repository.dao.Question;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class QuestionView {

  private final String question;

  private final byte[] image;

  private final List<AnswerView> answers;

  private final Long answerTime;

  public QuestionView(Question question) {
    this.question = question.getQuestion();
    this.image = question.getImage();
    this.answerTime = question.getAnswerTime();
    this.answers = question.getAnswers().stream()
        .map(a -> new AnswerView(a.getId(), a.getAnswer()))
        .collect(Collectors.toList());
  }
}
