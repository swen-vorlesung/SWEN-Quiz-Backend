package de.doubleslash.quiz.engine.score;

public interface ScoreCalculator {

  int calculateScore(long offset, long answerTime, int correctAnswers, int wrongAnswers);

}
