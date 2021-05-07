package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.Role.Constants.ADMIN;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.service.GroupService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupMutation implements GraphQLMutationResolver {
  private final GroupService groupService;


  GroupView createGroup(GroupEdit group) {
    return groupService.createGroup(group);
  }
}
