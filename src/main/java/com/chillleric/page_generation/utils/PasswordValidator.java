package com.chillleric.page_generation.utils;

import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import com.chillleric.page_generation.constant.TypeValidation;
import com.chillleric.page_generation.exception.InvalidRequestException;

public class PasswordValidator {

    public static void validatePassword(Map<String, String> errorObject, String password,
            String keyError) {
        if (!Base64.isBase64(password)) {
            errorObject.put(keyError, "Password must be encoded!");
            throw new InvalidRequestException(errorObject, "Password must be encoded!");
        } else {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(password));
                if (!decodedNewPassword.matches(TypeValidation.PASSWORD)) {
                    errorObject.put(keyError, "Password must be passed condition!");
                    throw new InvalidRequestException(errorObject,
                            "Password must be passed condition!");
                }
            } catch (IllegalArgumentException e) {
                errorObject.put(keyError, "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            } catch (IllegalStateException e) {
                errorObject.put(keyError, "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            }
        }
    }

    public static void validateNewPassword(Map<String, String> errorObject, String newPassword,
            String keyError) {
        if (Base64.isBase64(newPassword)) {
            try {
                if (!newPassword.matches(TypeValidation.BASE64_REGEX)) {
                    errorObject.put(keyError, "Password must be encoded!");
                    throw new InvalidRequestException(errorObject, "Password must be encoded!");
                }
                String decodedNewPassword = new String(Base64.decodeBase64(newPassword));
                if (!decodedNewPassword.matches(TypeValidation.PASSWORD)) {
                    errorObject.put(keyError, "Password must be passed condition!");
                    throw new InvalidRequestException(errorObject,
                            "Password must be passed condition!");
                }
            } catch (IllegalArgumentException e) {
                errorObject.put(keyError, "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            } catch (IllegalStateException e) {
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            }
        } else {
            errorObject.put(keyError, "Password must be encoded!");
            throw new InvalidRequestException(errorObject, "Password must be encoded!");
        }
    }
}
