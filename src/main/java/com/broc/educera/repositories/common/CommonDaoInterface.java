package com.broc.educera.repositories.common;

import java.util.Optional;

public interface CommonDaoInterface <T, ID>{
    Optional<T> findById(ID id);
    <S extends T> S save(S entity);
    void deleteById(ID id);
}
