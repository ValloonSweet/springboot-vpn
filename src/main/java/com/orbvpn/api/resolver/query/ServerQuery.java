package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.service.ServerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerQuery implements GraphQLQueryResolver {
  private final ServerService serverService;

  public List<ServerView> servers() {
    return serverService.getServers();
  }
}
