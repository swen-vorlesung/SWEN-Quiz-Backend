package de.doubleslash.quiz.engine.score;

public class SimpleScoreCalculator implements ScoreCalculator {

  @Override
  public int calculateScore(Long offset, boolean isAnswerCorrect) {
    if (isAnswerCorrect) {
      return Math.max(0, offset.intValue());
    }
    return 0;
  }
}
