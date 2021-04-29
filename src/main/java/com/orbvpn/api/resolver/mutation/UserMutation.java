package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.LoginCredentials;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMutation implements GraphQLMutationResolver {


  private final UserService userService;

  public UserView register(UserCreate userCreate) {
    return userService.register(userCreate);
  }

  public AuthenticatedUser login(LoginCredentials loginCredentials) {
    return userService.login(loginCredentials);
  }

  public boolean forgotPassword(String email) {
    return userService.forgotPassword(email);
  }

  public boolean resetPassword(String token, String password) {
    return userService.resetPassword(token, password);
  }
}
