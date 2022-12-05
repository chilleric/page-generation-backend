package com.chillleric.page_generation.service.login;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.constant.TypeValidation;
import com.chillleric.page_generation.dto.login.LoginRequest;
import com.chillleric.page_generation.dto.login.LoginResponse;
import com.chillleric.page_generation.dto.login.RegisterRequest;
import com.chillleric.page_generation.email.EmailDetail;
import com.chillleric.page_generation.email.EmailService;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.exception.ResourceNotFoundException;
import com.chillleric.page_generation.exception.UnauthorizedException;
import com.chillleric.page_generation.inventory.user.UserInventory;
import com.chillleric.page_generation.repository.code.Code;
import com.chillleric.page_generation.repository.code.CodeRepository;
import com.chillleric.page_generation.repository.code.TypeCode;
import com.chillleric.page_generation.repository.user.User;
import com.chillleric.page_generation.repository.user.UserRepository;
import com.chillleric.page_generation.service.AbstractService;
import com.chillleric.page_generation.utils.PasswordValidator;

@Service
public class LoginServiceImpl extends AbstractService<UserRepository> implements LoginService {

  @Value("${default.password}")
  protected String defaultPassword;

  @Autowired
  private EmailService emailService;

  @Autowired
  private CodeRepository codeRepository;

  @Autowired
  private UserInventory userInventory;

  @Override
  public Optional<LoginResponse> login(LoginRequest loginRequest, boolean isRegister) {
    validate(loginRequest);
    User user = new User();
    boolean normalUsername = true;
    if (loginRequest.getUsername().matches(TypeValidation.EMAIL)) {
      user = userInventory.findUserByEmail(loginRequest.getUsername())
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
      normalUsername = false;
    }
    if (loginRequest.getUsername().matches(TypeValidation.PHONE)) {
      user = userInventory.findUserByPhone(loginRequest.getUsername()).orElseThrow(
          () -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_PHONE_NUMBER));
      normalUsername = false;
    }
    if (normalUsername) {
      user = userInventory.findUserByUsername(loginRequest.getUsername())
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USERNAME));
    }
    Map<String, String> error = generateError(LoginRequest.class);
    PasswordValidator.validatePassword(generateError(LoginRequest.class),
        loginRequest.getPassword(), "password");
    if (user.getDeleted() == 1) {
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    if (!user.isVerified()) {
      return Optional.of(new LoginResponse("", "", false, true));
    }
    if (!bCryptPasswordEncoder().matches(loginRequest.getPassword(),
        user.getPassword())) {
      error.put("password", LanguageMessageKey.PASSWORD_NOT_MATCH);
      throw new InvalidRequestException(error, LanguageMessageKey.PASSWORD_NOT_MATCH);
    }
    Date now = new Date();
    if (user.isVerify2FA()) {
      String verify2FACode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
      emailService
          .sendSimpleMail(new EmailDetail(user.getEmail(), verify2FACode,
              "OTP"));
      Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
      Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
          TypeCode.VERIFY2FA.name());
      if (codes.isPresent()) {
        Code code = codes.get();
        code.setCode(verify2FACode);
        code.setExpiredDate(expiredDate);
        codeRepository.insertAndUpdateCode(code);
      } else {
        Code code = new Code(null, user.get_id(), TypeCode.VERIFY2FA, verify2FACode, expiredDate);
        codeRepository.insertAndUpdateCode(code);
      }
      return Optional.of(new LoginResponse("", "", true, false));
    } else {
      String deviceId = UUID.randomUUID().toString();
      Date expiredDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L);
      Map<String, Date> tokens = user.getTokens();
      tokens.put(deviceId, expiredDate);
      repository.insertAndUpdate(user);
      return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false, false));
    }
  }

  @Override
  public void logout(String id, String deviceId) {
    User user = userInventory.findUserById(id)
        .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
    if (user.getTokens() != null) {
      user.getTokens().remove(deviceId);
    }
    repository.insertAndUpdate(user);
  }

  @Override
  public void register(RegisterRequest registerRequest) {
    validate(registerRequest);
    Map<String, String> error = generateError(RegisterRequest.class);
    userInventory.findUserByUsername(registerRequest.getUsername()).ifPresent(username -> {
      error.put("username", LanguageMessageKey.USERNAME_EXISTED);
      throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
    });
    userInventory.findUserByEmail(registerRequest.getEmail()).ifPresent(userEmail -> {
      if (userEmail.isVerified()) {
        error.put("email", LanguageMessageKey.EMAIL_TAKEN);
        throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);
      } else {
        error.put("email", LanguageMessageKey.PLEASE_VERIFY_EMAIL);
        throw new InvalidRequestException(error, LanguageMessageKey.PLEASE_VERIFY_EMAIL);
      }
    });
    userInventory.findUserByPhone(registerRequest.getPhone()).ifPresent(userPhone -> {
      error.put("phone", LanguageMessageKey.PHONE_TAKEN);
      throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
    });
    PasswordValidator.validateNewPassword(error, registerRequest.getPassword(), "password");
    String passwordEncode = bCryptPasswordEncoder().encode(registerRequest.getPassword());
    User user = objectMapper.convertValue(registerRequest, User.class);
    ObjectId newId = new ObjectId();
    user.set_id(newId);
    user.setPassword(passwordEncode);
    user.setTokens(new HashMap<>());
    user.setGender(0);
    user.setDob("");
    repository.insertAndUpdate(user);
    String newCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
    Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
        TypeCode.REGISTER.name());
    if (codes.isPresent()) {
      Code code = codes.get();
      code.setCode(newCode);
      code.setExpiredDate(expiredDate);
      codeRepository.insertAndUpdateCode(code);
    } else {
      Code code = new Code(null, user.get_id(), TypeCode.REGISTER, newCode, expiredDate);
      codeRepository.insertAndUpdateCode(code);
    }
    emailService
        .sendSimpleMail(new EmailDetail(user.getEmail(), newCode, "OTP"));

  }

  @Override
  public void verifyRegister(String inputCode, String email) {
    User user = new User();
    if (email.matches(TypeValidation.EMAIL)) {
      user = userInventory.findUserByEmail(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    }
    Date now = new Date();
    Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
        TypeCode.REGISTER.name());
    if (codes.isPresent()) {
      Code code = codes.get();
      if (code.getCode().compareTo(inputCode) != 0) {
        throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
      } else if (code.getExpiredDate().compareTo(now) < 0) {
        throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
      }
    } else {
      throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
    }
    user.setVerified(true);
    repository.insertAndUpdate(user);
  }

  @Override
  public void resendVerifyRegister(String email) {
    User userCheckMail = userInventory.findUserByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    String newCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
    Optional<Code> codes = codeRepository.getCodesByType(userCheckMail.get_id().toString(),
        TypeCode.REGISTER.name());
    if (codes.isPresent()) {
      Code code = codes.get();
      code.setCode(newCode);
      code.setExpiredDate(expiredDate);
      codeRepository.insertAndUpdateCode(code);
    } else {
      Code code = new Code(null, userCheckMail.get_id(), TypeCode.REGISTER, newCode, expiredDate);
      codeRepository.insertAndUpdateCode(code);
    }
    emailService
        .sendSimpleMail(new EmailDetail(userCheckMail.getEmail(), newCode,
            "OTP"));

  }

  @Override
  public void forgotPassword(String email) {
    User user = new User();
    if (email.matches(TypeValidation.EMAIL)) {
      user = userInventory.findUserByEmail(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else if (email.matches(TypeValidation.PHONE)) {
      user = userInventory.findUserByPhone(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else {
      user = userInventory.findUserByUsername(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    }
    user
        .setPassword(
            bCryptPasswordEncoder().encode(
                Base64.getEncoder().encodeToString(defaultPassword.getBytes())));
    repository.insertAndUpdate(user);
    emailService.sendSimpleMail(new EmailDetail(user.getEmail(),
        "Username: " + user.getUsername() + " \n" + "Password: " + defaultPassword,
        "New password!"));
  }

  @Override
  public Optional<LoginResponse> verify2FA(String email, String inputCode) {
    User user = new User();
    if (email.matches(TypeValidation.EMAIL)) {
      user = userInventory.findUserByEmail(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else if (email.matches(TypeValidation.PHONE)) {
      user = userInventory.findUserByPhone(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else {
      user = userInventory.findUserByUsername(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    }
    Date now = new Date();
    Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
        TypeCode.VERIFY2FA.name());
    if (codes.isPresent()) {
      Code code = codes.get();
      if (code.getCode().compareTo(inputCode) != 0) {
        throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
      } else if (code.getExpiredDate().compareTo(now) < 0) {
        throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
      }
    } else {
      throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
    }
    String deviceId = UUID.randomUUID().toString();
    Map<String, Date> devices = user.getTokens();
    Date expiredDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L);
    devices.put(deviceId, expiredDate);
    repository.insertAndUpdate(user);
    return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false, false));
  }

  @Override
  public void resend2FACode(String email) {
    User user = new User();
    if (email.matches(TypeValidation.EMAIL)) {
      user = userInventory.findUserByEmail(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else if (email.matches(TypeValidation.PHONE)) {
      user = userInventory.findUserByPhone(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    } else {
      user = userInventory.findUserByUsername(email)
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
    }
    String newCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
    Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
        TypeCode.VERIFY2FA.name());
    if (codes.isPresent()) {
      Code code = codes.get();
      code.setCode(newCode);
      code.setExpiredDate(expiredDate);
      codeRepository.insertAndUpdateCode(code);
    } else {
      Code code = new Code(null, user.get_id(), TypeCode.VERIFY2FA, newCode, expiredDate);
      codeRepository.insertAndUpdateCode(code);
    }
    emailService
        .sendSimpleMail(new EmailDetail(user.getEmail(), newCode,
            "OTP"));
  }

}