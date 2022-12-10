package com.chillleric.page_generation.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.CommonResponse;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.common.ValidationResult;
import com.chillleric.page_generation.dto.user.UserRequest;
import com.chillleric.page_generation.dto.user.UserResponse;
import com.chillleric.page_generation.service.user.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "user")
public class UserController extends AbstractController<UserService> {

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "add-new-user")
        public ResponseEntity<CommonResponse<String>> addNewUser(@RequestBody UserRequest userRequest,
                        HttpServletRequest request) {
                ValidationResult result = validateToken(request);

                service.createNewUser(userRequest, result.getLoginId());
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, LanguageMessageKey.CREATE_USER_SUCCESS,
                                                HttpStatus.OK.value()),

                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "get-detail-user")
        public ResponseEntity<CommonResponse<UserResponse>> getUserDetail(
                        @RequestParam(required = true) String id,
                        HttpServletRequest request) {
                ValidationResult result = validateToken(request);
                if (id.compareTo(result.getLoginId()) == 0) {
                        return response(service.findOneUserById(id), LanguageMessageKey.SUCCESS);
                }
                return response(Optional.of(filterResponse(service.findOneUserById(id).get())),
                                LanguageMessageKey.SUCCESS);
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "get-list-users")
        public ResponseEntity<CommonResponse<ListWrapperResponse<UserResponse>>> getListUsers(
                        @RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam Map<String, String> allParams,
                        @RequestParam(defaultValue = "asc") String keySort,
                        @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
                ValidationResult result = validateToken(request);
                return response(service.getUsers(allParams, keySort, page, pageSize, "",
                                result.getLoginId()), LanguageMessageKey.SUCCESS);
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "update-user")
        public ResponseEntity<CommonResponse<String>> updateUser(@RequestBody UserRequest userRequest,
                        @RequestParam(required = true) String id, HttpServletRequest request) {
                service.updateUserById(id, userRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_USER_SUCCESS,
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "change-status-user")
        public ResponseEntity<CommonResponse<String>> changeStatusUser(@RequestParam String id,
                        HttpServletRequest request) {
                service.changeStatusUser(id);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, LanguageMessageKey.CHANGE_STATUS_USER_SUCCESS,
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

}