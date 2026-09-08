package cs.daasha.quiz_t.service;

import cs.daasha.quiz_t.dto.CreateQuestionRequest;
import cs.daasha.quiz_t.dto.OpenTdbQuestion;
import cs.daasha.quiz_t.dto.OpenTdbResponse;
import cs.daasha.quiz_t.dto.QuestionResponse;
import cs.daasha.quiz_t.entity.Choice;
import cs.daasha.quiz_t.entity.Question;
import cs.daasha.quiz_t.entity.QuestionType;
import cs.daasha.quiz_t.entity.QuizTournament;
import cs.daasha.quiz_t.repository.ChoiceRepository;
import cs.daasha.quiz_t.repository.QuestionRepository;
import cs.daasha.quiz_t.repository.QuizTournamentRepository;
import org.springframework.web.util.HtmlUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final QuizTournamentRepository tournamentRepository;
    private final RestTemplate restTemplate;

    public QuestionService(QuestionRepository questionRepository, ChoiceRepository choiceRepository,
                           QuizTournamentRepository tournamentRepository, RestTemplate restTemplate) {
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.tournamentRepository = tournamentRepository;
        this.restTemplate = restTemplate;
    }

    public QuestionResponse addManualQuestion(Long tournamentId, CreateQuestionRequest request) {
        QuizTournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        question.setType(request.getType());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setTournament(tournament);
        Question savedQuestion = questionRepository.save(question);

        for (String choiceText : request.getChoices()) {
            Choice choice = new Choice();
            choice.setChoiceText(choiceText);
            choice.setQuestion(savedQuestion);
            choiceRepository.save(choice);
        }

        return toResponse(savedQuestion, request.getChoices());
    }

    public List<QuestionResponse> fetchFromOpenTdb(Long tournamentId, String difficulty) {
        QuizTournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        String url = "https://opentdb.com/api.php?amount=10&difficulty=" + difficulty.toLowerCase() + "&type=multiple";
        OpenTdbResponse apiResponse = restTemplate.getForObject(url, OpenTdbResponse.class);

        List<QuestionResponse> responses = new ArrayList<>();

        for (OpenTdbQuestion otdb : apiResponse.getResults()) {
            String questionText = HtmlUtils.htmlUnescape(otdb.getQuestion());
            String correctAnswer = HtmlUtils.htmlUnescape(otdb.getCorrect_answer());

            List<String> choices = new ArrayList<>();
            choices.add(correctAnswer);
            for (String incorrect : otdb.getIncorrect_answers()) {
                choices.add(HtmlUtils.htmlUnescape(incorrect));
            }
            Collections.shuffle(choices);

            Question question = new Question();
            question.setQuestionText(questionText);
            question.setType(QuestionType.MULTIPLE);
            question.setCorrectAnswer(correctAnswer);
            question.setTournament(tournament);
            Question savedQuestion = questionRepository.save(question);

            for (String choiceText : choices) {
                Choice choice = new Choice();
                choice.setChoiceText(choiceText);
                choice.setQuestion(savedQuestion);
                choiceRepository.save(choice);
            }

            responses.add(toResponse(savedQuestion, choices));
        }

        return responses;
    }

    private QuestionResponse toResponse(Question question, List<String> choices) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setType(question.getType());
        response.setChoices(choices);
        return response;
    }
}