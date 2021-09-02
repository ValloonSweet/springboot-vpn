package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.service.ConnectionService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectionQuery implements GraphQLQueryResolver {
    private final ConnectionService connectionService;

    public List<ConnectionHistoryView> getConnectionHistory(Integer userId) {
        return connectionService.getConnectionHistory(userId);
    }

    public List<OnlineSessionView> getOnlineSessions(Integer userId) {
        return connectionService.getOnlineSessions(userId);
    }
}
