package cs.daasha.quiz_t.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenTdbResponse {
    private int response_code;
    private List<OpenTdbQuestion> results;
}