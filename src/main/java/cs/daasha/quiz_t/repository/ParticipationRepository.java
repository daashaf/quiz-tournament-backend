package cs.daasha.quiz_t.repository;

import cs.daasha.quiz_t.entity.Participation;
import cs.daasha.quiz_t.entity.QuizTournament;
import cs.daasha.quiz_t.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUser(User user);
    List<Participation> findByTournament(QuizTournament tournament);
}