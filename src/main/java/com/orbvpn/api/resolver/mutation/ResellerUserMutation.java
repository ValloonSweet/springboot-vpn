package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

import com.orbvpn.api.domain.dto.ResellerUserCreate;
import com.orbvpn.api.domain.dto.ResellerUserEdit;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.ResellerUserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class ResellerUserMutation implements GraphQLMutationResolver {

  private final ResellerUserService resellerUserService;

  @RolesAllowed({ADMIN, RESELLER})
  public UserView resellerCreateUser(ResellerUserCreate resellerUserCreate) {
    return resellerUserService.createUser(resellerUserCreate);
  }

  @RolesAllowed({ADMIN, RESELLER})
  public UserView resellerDeleteUser(int id) {
    return resellerUserService.deleteUser(id);
  }

  @RolesAllowed({ADMIN, RESELLER})
  public UserView resellerEditUser(int id, ResellerUserEdit resellerUserEdit) {
    return resellerUserService.editUser(id, resellerUserEdit);
  }
}
