package de.doubleslash.quiz.engine.exceptions;

public class QuizNotFoundException extends RuntimeException {

  public QuizNotFoundException() {
    super("The Quiz could not be found!");
  }
}
