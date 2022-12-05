package com.chillleric.page_generation.service.login;

import java.util.Optional;

import com.chillleric.page_generation.dto.login.LoginRequest;
import com.chillleric.page_generation.dto.login.LoginResponse;
import com.chillleric.page_generation.dto.login.RegisterRequest;

public interface LoginService {
    Optional<LoginResponse> login(LoginRequest loginRequest, boolean isRegister);

    void logout(String id, String deviceId);

    void register(RegisterRequest registerRequest);

    void verifyRegister(String code, String email);

    void resendVerifyRegister(String email);

    void forgotPassword(String email);

    Optional<LoginResponse> verify2FA(String email, String code);

    void resend2FACode(String email);
}