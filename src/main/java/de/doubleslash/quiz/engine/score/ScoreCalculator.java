package de.doubleslash.quiz.engine.score;

public interface ScoreCalculator {

  int calculateScore(Long offset, boolean isAnswerCorrect);
}
