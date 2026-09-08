package cs.daasha.quiz_t.dto;

import cs.daasha.quiz_t.entity.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionResponse {
    private Long id;
    private String questionText;
    private QuestionType type;
    private List<String> choices;
}