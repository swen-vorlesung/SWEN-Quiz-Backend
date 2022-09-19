package de.doubleslash.quiz.engine.controller;

public interface QuizHandler {

  String newQuiz(Long quizId);

  boolean addParticipant(String sessionId, String nickName);

  void notifyQuizWithAllParticipants(String sessionId);

  boolean startQuiz(String sessionId);

  boolean addParticipantInput(String sessionId, String nickname, Long answerId);

  boolean startNewQuestion(String sessionId);

  void notifyQuizStarted(String sessionId);
}
