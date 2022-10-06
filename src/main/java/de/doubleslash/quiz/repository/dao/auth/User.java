package de.doubleslash.quiz.repository.dao.auth;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USR01_USER")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr01_id_generator")
  @SequenceGenerator(name="usr01_id_generator", sequenceName = "usr01_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean enabled = true;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  @JsonManagedReference
  private Set<Quiz> quizzes;
}
