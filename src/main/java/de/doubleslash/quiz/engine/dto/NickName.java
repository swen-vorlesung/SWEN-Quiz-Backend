package de.doubleslash.quiz.engine.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NickName {

  @NotNull
  private String nickname;
}
