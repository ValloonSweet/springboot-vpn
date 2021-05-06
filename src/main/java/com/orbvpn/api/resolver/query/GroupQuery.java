package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.service.GroupService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupQuery implements GraphQLQueryResolver {
  private final GroupService groupService;

  List<GroupView> groups() {
    return groupService.getGroups();
  }
}
