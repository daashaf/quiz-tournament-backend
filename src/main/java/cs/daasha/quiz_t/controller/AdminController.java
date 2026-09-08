package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.RegisterRequest;
import cs.daasha.quiz_t.dto.TournamentResponse;
import cs.daasha.quiz_t.dto.UserResponse;
import cs.daasha.quiz_t.service.TournamentService;
import cs.daasha.quiz_t.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final TournamentService tournamentService;

    public AdminController(UserService userService, TournamentService tournamentService) {
        this.userService = userService;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/instructors")
    public ResponseEntity<UserResponse> createInstructor(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.createInstructor(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @DeleteMapping("/tournaments/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long tournamentId) {
        tournamentService.deleteTournament(tournamentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tournaments/{tournamentId}/likes")
    public ResponseEntity<Map<String, Long>> getLikesCount(@PathVariable Long tournamentId) {
        long count = tournamentService.getLikesCount(tournamentId);
        return ResponseEntity.ok(Map.of("likesCount", count));
    }
}