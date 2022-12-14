package com.chillleric.page_generation.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.CommonResponse;
import com.chillleric.page_generation.dto.common.ValidationResult;
import com.chillleric.page_generation.dto.login.LoginRequest;
import com.chillleric.page_generation.dto.login.LoginResponse;
import com.chillleric.page_generation.dto.login.RegisterRequest;
import com.chillleric.page_generation.jwt.JwtValidation;
import com.chillleric.page_generation.service.login.LoginService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "auth")
public class LoginController extends AbstractController<LoginService> {

        @Autowired
        private JwtValidation jwtValidation;

        @PostMapping(value = "login")
        public ResponseEntity<CommonResponse<LoginResponse>> login(
                        @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
                return response(service.login(loginRequest, false),
                                LanguageMessageKey.LOGIN_SUCCESS);
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "logout")
        public ResponseEntity<CommonResponse<String>> logout(HttpServletRequest request,
                        @RequestParam(required = true) String deviceId) {
                ValidationResult result = validateToken(request);
                service.logout(result.getLoginId(), deviceId);
                return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true,
                                null, LanguageMessageKey.LOGOUT_SUCCESS, HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "register")
        public ResponseEntity<CommonResponse<String>> signUp(
                        @RequestBody RegisterRequest registerRequest) {
                service.register(registerRequest);
                return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true,
                                null, LanguageMessageKey.SEND_VERIFY_EMAIL, HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "verify-email")
        public ResponseEntity<CommonResponse<String>> verifyEmail(
                        @RequestParam(required = true) String email,
                        @RequestParam(required = true) String code) {
                service.verifyRegister(code, email);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null,
                                                LanguageMessageKey.VERIFY_EMAIL_SUCCESS,
                                                HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "verify-email/resend")
        public ResponseEntity<CommonResponse<String>> resendVerifyEmail(
                        @RequestParam(required = true) String email) {
                service.resendVerifyRegister(email);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null,
                                                LanguageMessageKey.RESEND_VERIFY_EMAIL,
                                                HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "forgot-password")
        public ResponseEntity<CommonResponse<String>> forgotPassword(
                        @RequestParam(required = true) String email) {
                service.forgotPassword(email);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null,
                                                LanguageMessageKey.SEND_FORGOT_PASSWORD,
                                                HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "verify-2fa")
        public ResponseEntity<CommonResponse<LoginResponse>> verify2fa(
                        @RequestParam(required = true) String email,
                        @RequestParam(required = true) String code) {
                return response(service.verify2FA(email, code),
                                LanguageMessageKey.VERIFY_2FA_SUCCESS);
        }

        @PostMapping(value = "verify-2fa/resend")
        public ResponseEntity<CommonResponse<String>> resendVerify2fa(
                        @RequestParam(required = true) String email) {
                service.resend2FACode(email);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null,
                                                LanguageMessageKey.RESEND_VERIFY_EMAIL,
                                                HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }

        @PostMapping(value = "get-jwt")
        public ResponseEntity<CommonResponse<String>> getJwt(@RequestParam String deviceId,
                        @RequestParam String userId) {
                String jwt = jwtValidation.generateToken(userId, deviceId);
                return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true,
                                jwt, LanguageMessageKey.RESEND_VERIFY_EMAIL, HttpStatus.OK.value()),
                                null, HttpStatus.OK.value());
        }
}
