package de.doubleslash.quiz.transport.controller;

import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import de.doubleslash.quiz.transport.dto.Answers;

public interface QuizHandler {

  String newQuiz(Quiz quiz);

  boolean addParticipant(String sessionId, String nickName);

  void notifyQuizWithAllParticipants(String sessionId);

  boolean startQuiz(String sessionId);

  boolean addParticipantInput(String sessionId, String nickname, Answers answerId);

  boolean startNewQuestion(String sessionId);

  void notifyQuizStarted(String sessionId);

  boolean showResults(String sessionId);
}
