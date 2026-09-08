package cs.daasha.quiz_t.dto;

import cs.daasha.quiz_t.entity.Difficulty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TournamentResponse {
    private Long id;
    private String name;
    private String category;
    private Difficulty difficulty;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int minPassingScore;
    private String createdByUsername;
}