package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitResultResponse {
    private int score;
    private int totalQuestions;
    private List<QuestionFeedback> feedback;
}