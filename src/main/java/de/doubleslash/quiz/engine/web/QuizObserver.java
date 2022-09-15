package de.doubleslash.quiz.engine.web;

public interface QuizObserver {

  boolean newQuiz(Long quizId, String sessionId);

  boolean addParticipant(String sessionId, String nickName);

  boolean startQuiz(String sessionId);

  boolean addParticipantInput(String sessionId, String nickname, Long answerId);

  boolean startNewQuestion(String sessionId);
}
