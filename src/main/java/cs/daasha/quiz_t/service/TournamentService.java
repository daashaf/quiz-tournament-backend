package cs.daasha.quiz_t.service;

import cs.daasha.quiz_t.dto.*;
import cs.daasha.quiz_t.entity.*;
import cs.daasha.quiz_t.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final QuizTournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final TournamentLikeRepository tournamentLikeRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    public TournamentService(QuizTournamentRepository tournamentRepository, UserRepository userRepository,
                             ParticipationRepository participationRepository,
                             TournamentLikeRepository tournamentLikeRepository,
                             QuestionRepository questionRepository, ChoiceRepository choiceRepository) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.participationRepository = participationRepository;
        this.tournamentLikeRepository = tournamentLikeRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
    }

    public TournamentResponse createTournament(String instructorUsername, CreateTournamentRequest request) {
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        QuizTournament tournament = new QuizTournament();
        tournament.setName(request.getName());
        tournament.setCategory(request.getCategory());
        tournament.setDifficulty(request.getDifficulty());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setMinPassingScore(request.getMinPassingScore());
        tournament.setCreatedBy(instructor);

        QuizTournament saved = tournamentRepository.save(tournament);
        return toResponse(saved);
    }

    public List<TournamentResponse> getTournamentsByStatus(String username, String status) {
        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();

        List<QuizTournament> tournaments;

        switch (status.toLowerCase()) {
            case "upcoming" -> tournaments = tournamentRepository.findByStartDateAfter(now);
            case "past" -> tournaments = tournamentRepository.findByEndDateBefore(now);
            case "participated" -> tournaments = participationRepository.findByUser(player).stream()
                    .map(Participation::getTournament)
                    .collect(Collectors.toList());
            case "ongoing" -> {
                List<QuizTournament> ongoing = tournamentRepository.findByStartDateBeforeAndEndDateAfter(now, now);
                Set<Long> participatedIds = participationRepository.findByUser(player).stream()
                        .map(p -> p.getTournament().getId())
                        .collect(Collectors.toSet());
                tournaments = ongoing.stream()
                        .filter(t -> !participatedIds.contains(t.getId()))
                        .collect(Collectors.toList());
            }
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        }

        return tournaments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void likeTournament(String username, Long tournamentId) {
        User player = getPlayer(username);
        QuizTournament tournament = getTournament(tournamentId);

        boolean alreadyLiked = tournamentLikeRepository.findByUserAndTournament(player, tournament).isPresent();
        if (!alreadyLiked) {
            TournamentLike like = new TournamentLike();
            like.setUser(player);
            like.setTournament(tournament);
            tournamentLikeRepository.save(like);
        }
    }

    public void unlikeTournament(String username, Long tournamentId) {
        User player = getPlayer(username);
        QuizTournament tournament = getTournament(tournamentId);

        tournamentLikeRepository.findByUserAndTournament(player, tournament)
                .ifPresent(tournamentLikeRepository::delete);
    }

    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteTournament(Long tournamentId) {
        QuizTournament tournament = getTournament(tournamentId);

        List<Question> questions = questionRepository.findByTournament(tournament);
        for (Question question : questions) {
            List<Choice> choices = choiceRepository.findByQuestion(question);
            choiceRepository.deleteAll(choices);
        }
        questionRepository.deleteAll(questions);

        participationRepository.deleteAll(participationRepository.findByTournament(tournament));
        tournamentLikeRepository.deleteAll(tournamentLikeRepository.findByTournament(tournament));

        tournamentRepository.delete(tournament);
    }

    public long getLikesCount(Long tournamentId) {
        QuizTournament tournament = getTournament(tournamentId);
        return tournamentLikeRepository.countByTournament(tournament);
    }

    public TournamentScoresResponse getTournamentScores(Long tournamentId) {
        QuizTournament tournament = getTournament(tournamentId);
        List<Participation> participations = participationRepository.findByTournament(tournament);

        List<PlayerScoreEntry> playerScores = participations.stream()
                .map(p -> {
                    PlayerScoreEntry entry = new PlayerScoreEntry();
                    entry.setPlayerName(p.getUser().getFirstName() + " " + p.getUser().getLastName());
                    entry.setCompletedDate(p.getCompletedDate());
                    entry.setScore(p.getScore());
                    return entry;
                })
                .sorted(Comparator.comparingInt(PlayerScoreEntry::getScore).reversed())
                .collect(Collectors.toList());

        double averageScore = participations.stream()
                .mapToInt(Participation::getScore)
                .average()
                .orElse(0);

        TournamentScoresResponse response = new TournamentScoresResponse();
        response.setTotalPlayers(participations.size());
        response.setAverageScore(averageScore);
        response.setLikesCount(tournamentLikeRepository.countByTournament(tournament));
        response.setPlayerScores(playerScores);
        return response;
    }

    public List<TournamentResponse> getMyTournaments(String instructorUsername) {
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return tournamentRepository.findByCreatedBy(instructor).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TournamentResponse updateTournament(String instructorUsername, Long tournamentId, UpdateTournamentRequest request) {
        QuizTournament tournament = getTournament(tournamentId);
        if (!tournament.getCreatedBy().getUsername().equals(instructorUsername)) {
            throw new SecurityException("You can only update your own tournaments");
        }
        tournament.setName(request.getName());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        QuizTournament saved = tournamentRepository.save(tournament);
        return toResponse(saved);
    }

    public void deleteOwnTournament(String instructorUsername, Long tournamentId) {
        QuizTournament tournament = getTournament(tournamentId);
        if (!tournament.getCreatedBy().getUsername().equals(instructorUsername)) {
            throw new SecurityException("You can only delete your own tournaments");
        }
        deleteTournament(tournamentId);
    }

    private User getPlayer(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private QuizTournament getTournament(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
    }

    private TournamentResponse toResponse(QuizTournament t) {
        TournamentResponse response = new TournamentResponse();
        response.setId(t.getId());
        response.setName(t.getName());
        response.setCategory(t.getCategory());
        response.setDifficulty(t.getDifficulty());
        response.setStartDate(t.getStartDate());
        response.setEndDate(t.getEndDate());
        response.setMinPassingScore(t.getMinPassingScore());
        response.setCreatedByUsername(t.getCreatedBy().getUsername());
        return response;
    }
}