package de.doubleslash.quiz.engine.processor;

import de.doubleslash.quiz.transport.dto.Participant;
import java.util.List;

public interface QuizSocket {

  void sendResults(String sessionId, List<Participant> participants, boolean isFinished);

  void sendTimeOut(String sessionId);
}
