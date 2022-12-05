package com.chillleric.page_generation.repository.code;

import java.util.Optional;

public interface CodeRepository {
    Optional<Code> getCodesByCode(String userId, String code);

    Optional<Code> getCodesByType(String userId, String type);

    void insertAndUpdateCode(Code code);
}