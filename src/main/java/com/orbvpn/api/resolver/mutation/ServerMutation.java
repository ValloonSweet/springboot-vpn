package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.service.ServerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class ServerMutation implements GraphQLMutationResolver {
  private final ServerService serverService;

  public ServerView createServer(@Valid ServerEdit server) {
    return serverService.createServer(server);
  }
}
