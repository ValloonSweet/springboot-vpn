package com.orbvpn.api.resolver.query;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.service.ResellerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResellerQuery implements GraphQLQueryResolver {
  private final ResellerService resellerService;

  @RolesAllowed(ADMIN)
  public ResellerView reseller(int id) {
    return resellerService.getReseller(id);
  }

  @RolesAllowed(ADMIN)
  public List<ResellerView> resellers() {
    return resellerService.getEnabledResellers();
  }
}
