package de.doubleslash.quiz.transport.web.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SendAnsweredParticipantsEvent implements QuizEvent {

  private int answeredParticipants;
}
