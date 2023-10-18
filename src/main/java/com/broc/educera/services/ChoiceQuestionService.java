package com.broc.educera.services;

import com.broc.educera.entities.ChoiceQuestion;
import com.broc.educera.repositories.ChoiceQuestionRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChoiceQuestionService implements CommonDaoInterface<ChoiceQuestion, Long> {

    private final ChoiceQuestionRepository choiceQuestionRepository;

    @Override
    public Optional<ChoiceQuestion> findById(Long id) {
        return choiceQuestionRepository.findById(id);
    }

    @Override
    public <S extends ChoiceQuestion> S save(S choiceQuestion) {
        return choiceQuestionRepository.save(choiceQuestion);
    }

    @Override
    public void deleteById(Long id) {
        choiceQuestionRepository.deleteById(id);
    }

}
