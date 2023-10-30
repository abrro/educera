package com.broc.educera.services;

import com.broc.educera.dtos.requests.QuestionResponseRequest;
import com.broc.educera.entities.*;
import com.broc.educera.repositories.QuestionRepository;
import com.broc.educera.repositories.QuizParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class QuizParticipationService {

    private final QuizParticipationRepository quizParticipationRepository;
    private final QuestionRepository questionRepository;

    public Optional<QuizParticipation> findById(Long id) {
        return quizParticipationRepository.findById(id);
    }

    public QuizParticipation save(QuizParticipation quizParticipation) {
        return quizParticipationRepository.save(quizParticipation);
    }

    public Optional<QuizParticipation> findByStudentIdAndQuizId(Long student_id, Long quiz_id){
        return quizParticipationRepository.findByStudent_IdAndQuiz_Id(student_id, quiz_id);
    }

    public QuizParticipation calculateResults(List<QuestionResponseRequest> responses, Quiz quiz, QuizParticipation qp) throws Exception{
        List<Question> questions = questionRepository.findAllByQuizId(quiz.getId());
        List<QuestionResponse> questionResponses = new ArrayList<>();
        double totalPoints = 0.0;

        for (Question question : questions) {
            QuestionResponseRequest qrDto = responses.stream().
                    filter(res -> res.getQuestion_id().equals(question.getId())).collect(Collectors.toList()).get(0);
            Supplier<Stream<QuestionAnswer>> answerStream = () -> question.getQuestionAnswers().stream();

            double points = 0.0;
            QuestionResponse newQr = QuestionResponse.builder().response(qrDto.getResponse()).acquiredPoints(points).build();

            if (question.getQuestionType().equals(QuestionType.SINGLE_CHOICE) && !qrDto.getResponse().equals("")){
                String[] qIds = qrDto.getResponse().split(",");
                if (qIds.length > 1) {
                    throw new Exception("Invalid response : Sent more than one answer on single choice question with id:" + question.getId());
                } else {
                    long resId = Long.parseLong(qIds[0]);
                    if (answerStream.get().mapToLong(QuestionAnswer::getId).noneMatch(id -> id == resId)) {
                        throw new Exception("Invalid response : Selected answer with id: " + resId +
                                " is not among possible answers for question with id: " + question.getId());
                    } else {
                        points = answerStream.get().filter(a -> a.getId().equals(resId))
                                .mapToDouble(m -> m.getPercentageValue() * question.getMaximumPoints()).sum();
//                        points = answerStream.filter(a -> a.getId().equals(resId)).collect(Collectors.toList())
//                                .get(0).getPercentageValue() * question.getMaximumPoints();
                    }
                }
            }
            if (question.getQuestionType().equals(QuestionType.MULTI_CHOICE) && !qrDto.getResponse().equals("")) {
                String[] qIds = qrDto.getResponse().split(",");
                for (String qId : qIds) {
                    long resId = Long.parseLong(qId);
                    if (answerStream.get().mapToLong(QuestionAnswer::getId).noneMatch(id -> id == resId)) {
                        throw new Exception("Invalid response : Selected answer with id: " + resId +
                                " is not among possible answers for question with id: " + question.getId());
                    } else {
                        points += answerStream.get().filter(a -> a.getId().equals(resId))
                                .mapToDouble(m -> m.getPercentageValue() * question.getMaximumPoints()).sum();
//                        points = answerStream.filter(a -> a.getId().equals(resId)).collect(Collectors.toList())
//                                .get(0).getPercentageValue() * question.getMaximumPoints();
                    }
                }
            }
            if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
//                points = answerStream.filter(a -> a.getAnswer().equals(qrDto.getResponse()))
//                        .map(m -> m.getPercentageValue() * question.getMaximumPoints()).collect(Collectors.toList()).get(0);
                points = answerStream.get().filter(a -> {
                    if(question.isCaseSensitive()) {
                        return qrDto.getResponse().matches(a.getAnswer());
                    }else{
                        return Pattern.compile(Pattern.quote(a.getAnswer()), Pattern.CASE_INSENSITIVE).matcher(qrDto.getResponse()).find();
                    }
                }).findFirst().map(
                        questionAnswer -> questionAnswer.getPercentageValue() * question.getMaximumPoints()
                ).orElse(0.0);
            }
            totalPoints += points;
            newQr.setAcquiredPoints(points);
            newQr.setQuizParticipation(qp);
            newQr.setQuestion(question);
            questionResponses.add(newQr);
        }

        qp.setSubmissionTime(LocalDateTime.now());
        qp.setTotalPoints(totalPoints);
        qp.setQuizParticipationState(QuizParticipationState.SUBMITTED);
        qp.setQuestionResponses(questionResponses);
        //qp.setQuiz(quiz);

        return quizParticipationRepository.save(qp);
    }
}
