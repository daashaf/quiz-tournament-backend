package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.TournamentScoresResponse;
import cs.daasha.quiz_t.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments")
public class ScoreController {

    private final TournamentService tournamentService;

    public ScoreController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{tournamentId}/scores")
    public ResponseEntity<TournamentScoresResponse> getScores(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(tournamentService.getTournamentScores(tournamentId));
    }
}