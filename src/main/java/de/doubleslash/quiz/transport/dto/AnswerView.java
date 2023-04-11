package de.doubleslash.quiz.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerView {

  private Long id;

  private String answer;

  private Boolean isCorrect;
}
