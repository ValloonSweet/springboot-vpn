package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
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

  public AuthenticatedUser login(String email, String password) {
    return userService.login(email, password);
  }

  public boolean requestResetPassword(String email) {
    return userService.requestResetPassword(email);
  }

  public boolean resetPassword(String token, String password) {
    return userService.resetPassword(token, password);
  }
}
