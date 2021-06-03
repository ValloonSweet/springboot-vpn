package com.orbvpn.api.resolver.query;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsView;
import com.orbvpn.api.domain.dto.ResellerLevelView;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.entity.ResellerLevelCoefficients;
import com.orbvpn.api.service.ResellerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.math.BigDecimal;
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

  @RolesAllowed(ADMIN)
  public BigDecimal totalResellersCredit() {
    return resellerService.getTotalResellersCredit();
  }

  @RolesAllowed(ADMIN)
  public List<ResellerLevelView> getResellersLevels() {
    return resellerService.getResellersLevels();
  }

  @RolesAllowed(ADMIN)
  public ResellerLevelCoefficientsView getResellerLevelCoefficients() {
    return resellerService.getResellerLevelCoefficients();
  }
}
