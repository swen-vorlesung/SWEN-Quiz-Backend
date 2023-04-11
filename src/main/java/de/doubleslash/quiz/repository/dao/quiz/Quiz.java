package de.doubleslash.quiz.repository.dao.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.doubleslash.quiz.repository.dao.auth.User;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "QUI01_QUIZ")
public class Quiz {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qui01_id_generator")
  @SequenceGenerator(name="qui01_id_generator", sequenceName = "qui01_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER)
  @JsonManagedReference
  @OrderBy("id")
  private Set<Question> questions;

  private String name;

  @ManyToOne
  @JoinColumn(name = "USR01_ID")
  @JsonBackReference
  private User user;
}
