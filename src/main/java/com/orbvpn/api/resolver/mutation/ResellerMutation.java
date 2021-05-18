package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.service.ResellerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class ResellerMutation implements GraphQLMutationResolver {
  private final ResellerService resellerService;

  @RolesAllowed(ADMIN)
  public ResellerView createReseller(@Valid ResellerCreate reseller) {
    return resellerService.createReseller(reseller);
  }

  @RolesAllowed(ADMIN)
  public ResellerView editReseller(int id, @Valid ResellerEdit reseller) {
    return resellerService.editReseller(id, reseller);
  }

  @RolesAllowed(ADMIN)
  public ResellerView deleteReseller(int id) {
    return resellerService.deleteReseller(id);
  }

  @RolesAllowed(ADMIN)
  public ResellerView addResellerServiceGroup(int resellerId, int serviceGroupId) {
    return resellerService.addResellerServiceGroup(resellerId, serviceGroupId);
  }

  @RolesAllowed(ADMIN)
  public ResellerView removeResellerServiceGroup(int resellerId, int serviceGroupId) {
    return resellerService.removeResellerServiceGroup(resellerId, serviceGroupId);
  }
}
