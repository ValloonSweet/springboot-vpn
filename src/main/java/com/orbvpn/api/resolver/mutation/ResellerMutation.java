package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsEdit;
import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsView;
import com.orbvpn.api.domain.dto.ResellerLevelEdit;
import com.orbvpn.api.domain.dto.ResellerLevelView;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.service.ResellerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import java.math.BigDecimal;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
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

  @RolesAllowed(ADMIN)
  public ResellerView setResellerLevel(int resellerId, ResellerLevelName level) {
    return resellerService.setResellerLevel(resellerId, level);
  }

  @RolesAllowed(ADMIN)
  public ResellerView addResellerCredit(int resellerId,
    @DecimalMin(value = "0.0", inclusive = false)BigDecimal credit) {
    return resellerService.addResellerCredit(resellerId, credit);
  }

  @RolesAllowed(ADMIN)
  public ResellerView deductResellerCredit(int resellerId,
                                        @DecimalMin(value = "0.0", inclusive = false)BigDecimal credit) {
    return resellerService.deductResellerCredit(resellerId, credit);
  }

  @RolesAllowed(ADMIN)
  public ResellerLevelView updateResellerLevel(int id, ResellerLevelEdit level) {
    return resellerService.updateResellerLevel(id, level);
  }

  @RolesAllowed(ADMIN)
  public ResellerLevelCoefficientsView updateResellerLevelCoefficients(
    ResellerLevelCoefficientsEdit resellerLevelCoefficientsEdit) {
    return resellerService.updateResellerLevelCoefficients(resellerLevelCoefficientsEdit);
  }

}
