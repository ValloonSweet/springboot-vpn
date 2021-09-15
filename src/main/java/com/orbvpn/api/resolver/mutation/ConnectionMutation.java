package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.service.ConnectionService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectionMutation implements GraphQLMutationResolver {
    private final ConnectionService connectionService;

    public Boolean disconnect(String onlineSessionId) {
        return connectionService.disconnect(onlineSessionId);
    }
}
