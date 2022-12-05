package com.chillleric.page_generation.service.user;

import java.util.Map;
import java.util.Optional;

import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.user.UserRequest;
import com.chillleric.page_generation.dto.user.UserResponse;

public interface UserService {

    void createNewUser(UserRequest userRequest, String loginId);

    void updateUserById(String userId, UserRequest userRequest);

    Optional<UserResponse> findOneUserById(String userId);

    Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams,
            String keySort, int page,
            int pageSize, String sortField, String loginId);

    void changeStatusUser(String userId);
}