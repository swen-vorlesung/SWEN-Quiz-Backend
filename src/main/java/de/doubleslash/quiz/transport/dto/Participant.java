package de.doubleslash.quiz.transport.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Participant {

  private final String nickname;

  private int score = 0;

  private int gainedPoints = 0;

  public void addScore(int score) {
    this.gainedPoints = score;
    this.score += score;
  }
}
