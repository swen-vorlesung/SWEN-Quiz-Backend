package de.doubleslash.quiz.engine.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Participant {

  private final String nickname;

  private int score = 0;

  public void addScore(int score) {
    this.score += score;
  }
}
