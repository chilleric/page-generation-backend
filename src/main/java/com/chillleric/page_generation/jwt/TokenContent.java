package com.chillleric.page_generation.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenContent {
    private String userId;
    private String deviceId;
}
