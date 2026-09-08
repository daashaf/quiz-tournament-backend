package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerSubmission {
    private Long questionId;
    private String selectedAnswer;
}