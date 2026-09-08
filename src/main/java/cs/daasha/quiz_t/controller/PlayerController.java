package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.QuestionResponse;
import cs.daasha.quiz_t.dto.SubmitAnswersRequest;
import cs.daasha.quiz_t.dto.SubmitResultResponse;
import cs.daasha.quiz_t.dto.TournamentResponse;
import cs.daasha.quiz_t.service.ParticipationService;
import cs.daasha.quiz_t.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final TournamentService tournamentService;
    private final ParticipationService participationService;

    public PlayerController(TournamentService tournamentService, ParticipationService participationService) {
        this.tournamentService = tournamentService;
        this.participationService = participationService;
    }

    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentResponse>> getTournaments(
            Authentication authentication,
            @RequestParam String status) {
        String username = authentication.getName();
        List<TournamentResponse> tournaments = tournamentService.getTournamentsByStatus(username, status);
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping("/tournaments/{tournamentId}/play")
    public ResponseEntity<List<QuestionResponse>> playTournament(
            Authentication authentication,
            @PathVariable Long tournamentId) {
        String username = authentication.getName();
        List<QuestionResponse> questions = participationService.playTournament(username, tournamentId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/tournaments/{tournamentId}/submit")
    public ResponseEntity<SubmitResultResponse> submitAnswers(
            Authentication authentication,
            @PathVariable Long tournamentId,
            @Valid @RequestBody SubmitAnswersRequest request) {
        String username = authentication.getName();
        SubmitResultResponse result = participationService.submitAnswers(username, tournamentId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/tournaments/{tournamentId}/like")
    public ResponseEntity<Void> likeTournament(Authentication authentication, @PathVariable Long tournamentId) {
        tournamentService.likeTournament(authentication.getName(), tournamentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tournaments/{tournamentId}/like")
    public ResponseEntity<Void> unlikeTournament(Authentication authentication, @PathVariable Long tournamentId) {
        tournamentService.unlikeTournament(authentication.getName(), tournamentId);
        return ResponseEntity.ok().build();
    }
}