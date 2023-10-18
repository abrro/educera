package com.broc.educera.services;

import com.broc.educera.entities.User;
import com.broc.educera.repositories.UserRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements CommonDaoInterface<User, Long> {

    private final UserRepository userRepository;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    };

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public <S extends User> S save(S user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
