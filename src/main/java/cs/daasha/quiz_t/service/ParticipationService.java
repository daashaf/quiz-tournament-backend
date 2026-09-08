package cs.daasha.quiz_t.service;

import cs.daasha.quiz_t.dto.*;
import cs.daasha.quiz_t.entity.*;
import cs.daasha.quiz_t.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipationService {

    private final UserRepository userRepository;
    private final QuizTournamentRepository tournamentRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final ParticipationRepository participationRepository;

    public ParticipationService(UserRepository userRepository, QuizTournamentRepository tournamentRepository,
                                QuestionRepository questionRepository, ChoiceRepository choiceRepository,
                                ParticipationRepository participationRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.participationRepository = participationRepository;
    }

    public List<QuestionResponse> playTournament(String username, Long tournamentId) {
        User player = getPlayer(username);
        QuizTournament tournament = getTournament(tournamentId);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(tournament.getStartDate()) || now.isAfter(tournament.getEndDate())) {
            throw new IllegalStateException("Tournament is not currently ongoing");
        }
        if (hasAlreadyParticipated(player, tournament)) {
            throw new IllegalStateException("You have already participated in this tournament");
        }

        List<Question> questions = questionRepository.findByTournament(tournament);
        List<QuestionResponse> responses = new ArrayList<>();
        for (Question question : questions) {
            List<String> choiceTexts = choiceRepository.findByQuestion(question).stream()
                    .map(Choice::getChoiceText)
                    .toList();

            QuestionResponse response = new QuestionResponse();
            response.setId(question.getId());
            response.setQuestionText(question.getQuestionText());
            response.setType(question.getType());
            response.setChoices(choiceTexts);
            responses.add(response);
        }
        return responses;
    }

    public SubmitResultResponse submitAnswers(String username, Long tournamentId, SubmitAnswersRequest request) {
        User player = getPlayer(username);
        QuizTournament tournament = getTournament(tournamentId);

        if (hasAlreadyParticipated(player, tournament)) {
            throw new IllegalStateException("You have already participated in this tournament");
        }

        List<Question> questions = questionRepository.findByTournament(tournament);
        List<QuestionFeedback> feedbackList = new ArrayList<>();
        int score = 0;

        for (AnswerSubmission answer : request.getAnswers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Question not found in this tournament"));

            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer.getSelectedAnswer());
            if (isCorrect) {
                score++;
            }

            QuestionFeedback feedback = new QuestionFeedback();
            feedback.setQuestionId(question.getId());
            feedback.setQuestionText(question.getQuestionText());
            feedback.setSelectedAnswer(answer.getSelectedAnswer());
            feedback.setCorrect(isCorrect);
            feedback.setCorrectAnswer(isCorrect ? null : question.getCorrectAnswer());
            feedbackList.add(feedback);
        }

        Participation participation = new Participation();
        participation.setUser(player);
        participation.setTournament(tournament);
        participation.setScore(score);
        participation.setCompletedDate(LocalDateTime.now());
        participationRepository.save(participation);

        SubmitResultResponse result = new SubmitResultResponse();
        result.setScore(score);
        result.setTotalQuestions(questions.size());
        result.setFeedback(feedbackList);
        return result;
    }

    private User getPlayer(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private QuizTournament getTournament(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
    }

    private boolean hasAlreadyParticipated(User player, QuizTournament tournament) {
        return participationRepository.findByUser(player).stream()
                .anyMatch(p -> p.getTournament().getId().equals(tournament.getId()));
    }
}