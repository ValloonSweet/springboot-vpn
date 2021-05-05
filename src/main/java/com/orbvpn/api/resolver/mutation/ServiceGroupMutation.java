package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.Role.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.service.ServiceGroupService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceGroupMutation implements GraphQLMutationResolver {
  private final ServiceGroupService serviceGroupService;

  @RolesAllowed(ADMIN)
  ServiceGroupView createServiceGroup(ServiceGroupEdit serviceGroup) {
    return serviceGroupService.createServiceGroup(serviceGroup);
  }
}
