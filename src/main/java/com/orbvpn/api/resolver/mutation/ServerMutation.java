package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.service.ServerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerMutation implements GraphQLMutationResolver {
  private final ServerService serverService;

  public ServerView createServer(ServerEdit server) {
    return serverService.createServer(server);
  }
}
