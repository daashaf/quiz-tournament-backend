package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionFeedback {
    private Long questionId;
    private String questionText;
    private String selectedAnswer;
    private boolean correct;
    private String correctAnswer;
}