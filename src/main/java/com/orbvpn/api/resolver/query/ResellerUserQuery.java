package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.ResellerUserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResellerUserQuery implements GraphQLQueryResolver  {
  private final ResellerUserService resellerUserService;

  public UserView resellerGetUser(int id) {
    return resellerUserService.getUser(id);
  }

  public UserView resellerGetUserByEmail(String email) {
    return resellerUserService.getUserByEmail(email);
  }

  public Page<UserView> resellerGetUsers(int page, int size) {
    return resellerUserService.getUsers(page, size);
  }

  public Page<UserView> resellerGetExpiredUsers(int page, int size) {
    return resellerUserService.getExpiredUsers(page, size);
  }
}
