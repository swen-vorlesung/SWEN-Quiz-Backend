package de.doubleslash.quiz.transport.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Long id;

    private String question;

    private byte[] image;

    private List<AnswerDto> answers;

    private Long answerTime;
}
