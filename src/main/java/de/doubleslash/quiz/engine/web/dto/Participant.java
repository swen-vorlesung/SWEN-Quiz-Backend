package de.doubleslash.quiz.engine.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Participant {

  private final String nickname;

  private final int score = 0;

  public void addScore(int score) {
    score += score;
  }
}
