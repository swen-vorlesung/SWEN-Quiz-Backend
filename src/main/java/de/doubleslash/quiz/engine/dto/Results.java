package de.doubleslash.quiz.engine.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Results {

  private List<Participant> participants;

  private boolean isFinished;
}
