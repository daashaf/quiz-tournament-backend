package cs.daasha.quiz_t.repository;

import cs.daasha.quiz_t.entity.Question;
import cs.daasha.quiz_t.entity.QuizTournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTournament(QuizTournament tournament);
}