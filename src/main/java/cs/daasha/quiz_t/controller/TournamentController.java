package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.CreateTournamentRequest;
import cs.daasha.quiz_t.dto.TournamentResponse;
import cs.daasha.quiz_t.dto.UpdateTournamentRequest;
import cs.daasha.quiz_t.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/tournaments")
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody CreateTournamentRequest request,
            Authentication authentication) {
        String instructorUsername = authentication.getName();
        TournamentResponse response = tournamentService.createTournament(instructorUsername, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tournaments/my")
    public ResponseEntity<List<TournamentResponse>> getMyTournaments(Authentication authentication) {
        List<TournamentResponse> tournaments = tournamentService.getMyTournaments(authentication.getName());
        return ResponseEntity.ok(tournaments);
    }

    @PutMapping("/tournaments/{tournamentId}")
    public ResponseEntity<TournamentResponse> updateTournament(
            Authentication authentication,
            @PathVariable Long tournamentId,
            @Valid @RequestBody UpdateTournamentRequest request) {
        TournamentResponse response = tournamentService.updateTournament(authentication.getName(), tournamentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tournaments/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(Authentication authentication, @PathVariable Long tournamentId) {
        tournamentService.deleteOwnTournament(authentication.getName(), tournamentId);
        return ResponseEntity.ok().build();
    }
}