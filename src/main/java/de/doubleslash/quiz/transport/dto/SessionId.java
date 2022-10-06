package de.doubleslash.quiz.transport.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionId {

  @NotNull
  private String sessionId;

}
