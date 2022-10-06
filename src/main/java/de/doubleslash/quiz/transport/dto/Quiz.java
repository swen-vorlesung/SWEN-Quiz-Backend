package de.doubleslash.quiz.transport.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Quiz {

  private String name;

  private Long id;
}
