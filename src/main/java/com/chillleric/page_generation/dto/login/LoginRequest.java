package com.chillleric.page_generation.dto.login;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.chillleric.page_generation.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty(message = LanguageMessageKey.USERNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.USERNAME_REQUIRED)
    @NotNull(message = LanguageMessageKey.USERNAME_REQUIRED)
    private String username;

    @NotNull(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotBlank(message = LanguageMessageKey.PASSWORD_REQUIRED)
    private String password;
}
