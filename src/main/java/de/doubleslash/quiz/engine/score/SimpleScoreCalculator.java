package de.doubleslash.quiz.engine.score;

public class SimpleScoreCalculator implements ScoreCalculator {

  @Override
  public int calculateScore(long offset, long answerTime, int correctAnswers, int wrongAnswers) {
    return (int) Math.max(0, ((correctAnswers - wrongAnswers) * Math.max(1, answerTime - offset)));
  }
}
