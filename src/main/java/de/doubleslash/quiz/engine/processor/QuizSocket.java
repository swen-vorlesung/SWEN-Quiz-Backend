package de.doubleslash.quiz.engine.processor;

import de.doubleslash.quiz.engine.web.dto.Participant;
import java.util.List;

public interface QuizSocket {

  void sendResults(List<Participant> participants, boolean isFinished);
}
