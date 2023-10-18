package com.broc.educera.services;

import com.broc.educera.entities.ShortAnswerQuestion;
import com.broc.educera.repositories.ShortAnswerQuestionRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortAnswerQuestionService implements CommonDaoInterface<ShortAnswerQuestion, Long> {

    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    @Override
    public Optional<ShortAnswerQuestion> findById(Long id) {
        return shortAnswerQuestionRepository.findById(id);
    }

    @Override
    public <S extends ShortAnswerQuestion> S save(S shortAnswerQuestion) {
        return shortAnswerQuestionRepository.save(shortAnswerQuestion);
    }

    @Override
    public void deleteById(Long id) {
        shortAnswerQuestionRepository.deleteById(id);
    }

}
