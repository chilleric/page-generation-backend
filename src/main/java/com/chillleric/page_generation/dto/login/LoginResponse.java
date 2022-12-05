package com.chillleric.page_generation.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String userId;
    private String deviceId;
    private boolean isVerify2Fa;
    private boolean needVerify;
}
