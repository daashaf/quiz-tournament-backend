package cs.daasha.quiz_t.repository;

import cs.daasha.quiz_t.entity.QuizTournament;
import cs.daasha.quiz_t.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizTournamentRepository extends JpaRepository<QuizTournament, Long> {

    List<QuizTournament> findByStartDateAfter(LocalDateTime now);
    List<QuizTournament> findByEndDateBefore(LocalDateTime now);
    List<QuizTournament> findByStartDateBeforeAndEndDateAfter(LocalDateTime now1, LocalDateTime now2);
    List<QuizTournament> findByCreatedBy(User instructor);
}