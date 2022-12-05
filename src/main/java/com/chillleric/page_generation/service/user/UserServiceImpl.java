package com.chillleric.page_generation.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chillleric.page_generation.constant.DateTime;
import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.user.UserRequest;
import com.chillleric.page_generation.dto.user.UserResponse;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.exception.ResourceNotFoundException;
import com.chillleric.page_generation.inventory.user.UserInventory;
import com.chillleric.page_generation.repository.user.User;
import com.chillleric.page_generation.repository.user.UserRepository;
import com.chillleric.page_generation.service.AbstractService;
import com.chillleric.page_generation.utils.DateFormat;

@Service
public class UserServiceImpl extends AbstractService<UserRepository> implements UserService {
    @Value("${default.password}")
    protected String defaultPassword;

    @Autowired
    private UserInventory userInventory;

    @Override
    public void createNewUser(UserRequest userRequest, String loginId) {
        validate(userRequest);
        Map<String, String> error = generateError(UserRequest.class);
        userInventory.findUserByUsername(userRequest.getUsername()).ifPresent(thisName -> {
            error.put("username", LanguageMessageKey.USERNAME_EXISTED);
            throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
        });
        Date currentTime = DateFormat.getCurrentTime();
        User user = objectMapper.convertValue(userRequest, User.class);
        ObjectId newId = new ObjectId();
        user.set_id(newId);
        user.setPassword(
                bCryptPasswordEncoder().encode(
                        Base64.getEncoder().encodeToString(defaultPassword.getBytes())));
        user.setTokens(new HashMap<>());
        user.setCreated(currentTime);
        user.setModified(currentTime);

        repository.insertAndUpdate(user);
    }

    @Override
    public void updateUserById(String userId, UserRequest userRequest) {
        User user = userInventory.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        validate(userRequest);
        Map<String, String> error = generateError(UserRequest.class);
        userInventory.findUserByEmail(userRequest.getEmail()).ifPresent(thisEmail -> {
            if (thisEmail.get_id().compareTo(user.get_id()) != 0) {
                error.put("email", LanguageMessageKey.EMAIL_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);
            }
        });
        userInventory.findUserByPhone(userRequest.getPhone()).ifPresent(thisPhone -> {
            if (thisPhone.get_id().compareTo(user.get_id()) != 0) {
                error.put("phone", LanguageMessageKey.PHONE_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
            }
        });
        userInventory.findUserByUsername(userRequest.getUsername()).ifPresent(thisUsername -> {
            if (thisUsername.get_id().compareTo(user.get_id()) != 0) {
                error.put("username", LanguageMessageKey.USERNAME_EXISTED);
                throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
            }
        });
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        Date currentTime = DateFormat.getCurrentTime();
        User newUser = objectMapper.convertValue(userRequest, User.class);
        newUser.setPassword(user.getPassword());
        newUser.setTokens(user.getTokens());
        newUser.setCreated(user.getCreated());
        newUser.setModified(currentTime);
        newUser.setVerified(user.isVerified());
        newUser.setVerify2FA(user.isVerify2FA());
        newUser.set_id(user.get_id());

        repository.insertAndUpdate(newUser);
    }

    @Override
    public Optional<UserResponse> findOneUserById(String userId) {
        User user = userInventory.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        return Optional.of(
                new UserResponse(user.get_id().toString(), user.getUsername(), user.getPassword(),
                        user.getGender(), user.getDob(), user.getAddress(), user.getFirstName(),
                        user.getLastName(), user.getEmail(), user.getPhone(), user.getTokens(),
                        DateFormat.toDateString(user.getCreated(),
                                DateTime.YYYY_MM_DD),
                        DateFormat.toDateString(user.getModified(), DateTime.YYYY_MM_DD), user.isVerified(),
                        user.isVerify2FA(), user.getDeleted()));
    }

    @Override
    public Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams,
            String keySort, int page,
            int pageSize, String sortField,
            String loginId) {
        if (allParams.containsKey("_id")) {
            String[] idList = allParams.get("_id").split(",");
            ArrayList<String> check = new ArrayList<>(Arrays.asList(idList));
            if (check.size() == 0) {
                return Optional.of(
                        new ListWrapperResponse<UserResponse>(new ArrayList<>(), page, pageSize, 0));
            }
            allParams.put("_id", generateParamsValue(check));
        }

        List<User> users = repository.getUsers(allParams, "", page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<UserResponse>(
                users.stream().map(user -> new UserResponse(user.get_id().toString(), user.getUsername(),
                        user.getPassword(), user.getGender(), user.getDob(), user.getAddress(),
                        user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(),
                        user.getTokens(), DateFormat.toDateString(user.getCreated(),
                                DateTime.YYYY_MM_DD),
                        DateFormat.toDateString(user.getModified(), DateTime.YYYY_MM_DD),
                        user.isVerified(), user.isVerify2FA(), user.getDeleted())).collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }

    @Override
    public void changeStatusUser(String userId) {
        User user = userInventory.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        user.setDeleted(user.getDeleted() == 0 ? 1 : 0);
        user.setModified(DateFormat.getCurrentTime());

        repository.insertAndUpdate(user);
    }

}
