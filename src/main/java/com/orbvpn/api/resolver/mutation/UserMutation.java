package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.config.security.Unsecured;
import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import static com.orbvpn.api.domain.ValidationProperties.BAD_PASSWORD_MESSAGE;
import static com.orbvpn.api.domain.ValidationProperties.PASSWORD_PATTERN;

@Component
@RequiredArgsConstructor
@Validated
public class UserMutation implements GraphQLMutationResolver {


  private final UserService userService;

  @Unsecured
  public AuthenticatedUser register(@Valid UserRegister userRegister) {
    return userService.register(userRegister);
  }

  @Unsecured
  public AuthenticatedUser login(@Email String email, @NotBlank String password) {
    return userService.login(email, password);
  }

  @Unsecured
  public boolean requestResetPassword(@Email String email) {
    return userService.requestResetPassword(email);
  }

  @Unsecured
  public boolean resetPassword(@NotBlank String token,
    @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE) String password) {
    return userService.resetPassword(token, password);
  }

  public boolean changePassword(@Positive int id, String oldPassword,
    @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE) String password) {
    return userService.changePassword(id, oldPassword, password);
  }

  public UserProfileView editProfile(UserProfileEdit userProfile) {
    return userService.editProfile(userProfile);
  }

  public Boolean editAutoRenew(Boolean isActive) {
    return userService.editAutoRenew(isActive);
  }
}
