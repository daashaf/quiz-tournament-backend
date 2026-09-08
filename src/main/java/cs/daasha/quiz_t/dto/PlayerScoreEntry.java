package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlayerScoreEntry {
    private String playerName;
    private LocalDateTime completedDate;
    private int score;
}