package cs.daasha.quiz_t.repository;

import cs.daasha.quiz_t.entity.Choice;
import cs.daasha.quiz_t.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    List<Choice> findByQuestion(Question question);
}