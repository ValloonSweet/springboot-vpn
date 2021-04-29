package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMutation implements GraphQLMutationResolver {


  private final UserService userService;

  public UserView register(UserCreate userCreate) {
    return userService.register(userCreate);
  }
}
