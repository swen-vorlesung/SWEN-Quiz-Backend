package de.doubleslash.quiz.repository.dao.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.doubleslash.quiz.transport.dto.AnswerDto;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "QUI03_ANSWER")
public class Answer {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qui03_id_generator")
  @SequenceGenerator(name="qui03_id_generator", sequenceName = "qui03_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "QUI02_ID")
  @JsonBackReference
  private Question question;

  private String answer;

  private Boolean isCorrect;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }

    if (object.getClass() == (AnswerDto.class)) {
      return Objects.equals(id, ((AnswerDto) object).getId());
    } else if (object.getClass() != getClass()) {
      return false;
    }

    Answer answer = (Answer) object;
    return Objects.equals(id, answer.id);
  }
}
