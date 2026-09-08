package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TournamentScoresResponse {
    private int totalPlayers;
    private double averageScore;
    private long likesCount;
    private List<PlayerScoreEntry> playerScores;
}