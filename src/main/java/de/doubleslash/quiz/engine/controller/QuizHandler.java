package de.doubleslash.quiz.engine.controller;

import de.doubleslash.quiz.engine.dto.Answers;
import de.doubleslash.quiz.engine.repository.dao.quiz.Quiz;

public interface QuizHandler {

  String newQuiz(Quiz quiz);

  boolean addParticipant(String sessionId, String nickName);

  void notifyQuizWithAllParticipants(String sessionId);

  boolean startQuiz(String sessionId);

  boolean addParticipantInput(String sessionId, String nickname, Answers answerId);

  boolean startNewQuestion(String sessionId);

  void notifyQuizStarted(String sessionId);
}
