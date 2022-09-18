package de.doubleslash.quiz.engine.processor;

import de.doubleslash.quiz.engine.dto.Participant;
import java.util.List;

public interface QuizSocket {

  void sendResults(String sessionId, List<Participant> participants, boolean isFinished);
}
