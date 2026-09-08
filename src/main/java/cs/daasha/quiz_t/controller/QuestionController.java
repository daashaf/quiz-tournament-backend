package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.CreateQuestionRequest;
import cs.daasha.quiz_t.dto.QuestionResponse;
import cs.daasha.quiz_t.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/tournaments/{tournamentId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> addManualQuestion(
            @PathVariable Long tournamentId,
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = questionService.addManualQuestion(tournamentId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/opentdb")
    public ResponseEntity<List<QuestionResponse>> fetchFromOpenTdb(
            @PathVariable Long tournamentId,
            @RequestParam String difficulty) {
        List<QuestionResponse> responses = questionService.fetchFromOpenTdb(tournamentId, difficulty);
        return ResponseEntity.ok(responses);
    }
}