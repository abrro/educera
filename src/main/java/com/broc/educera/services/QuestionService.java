package com.broc.educera.services;

import com.broc.educera.entities.*;
import com.broc.educera.repositories.QuestionRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService implements CommonDaoInterface<Question, Long> {

    private final QuestionRepository questionRepository;

    @Override
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public <S extends Question> S save(S question) {
        return questionRepository.save(question);
    }

    public List<Question> findAllByQuizId(Long quiz_id) {
        return questionRepository.findAllByQuizId(quiz_id);
    }

}
