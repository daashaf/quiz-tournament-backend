package cs.daasha.quiz_t.repository;

import cs.daasha.quiz_t.entity.QuizTournament;
import cs.daasha.quiz_t.entity.TournamentLike;
import cs.daasha.quiz_t.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentLikeRepository extends JpaRepository<TournamentLike, Long> {

    Optional<TournamentLike> findByUserAndTournament(User user, QuizTournament tournament);
    long countByTournament(QuizTournament tournament);
    List<TournamentLike> findByTournament(QuizTournament tournament);
}