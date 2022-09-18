package de.doubleslash.quiz.engine.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Quiz {

  private String name;

  private Long id;
}
