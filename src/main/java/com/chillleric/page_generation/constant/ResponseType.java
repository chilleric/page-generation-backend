package com.chillleric.page_generation.constant;

public enum ResponseType {
    PUBLIC("public"),
    PRIVATE("private");

    private String responseType;

    ResponseType(String type) {
        this.responseType = type;
    }

    public String getResponseType() {
        return responseType;
    }
}
