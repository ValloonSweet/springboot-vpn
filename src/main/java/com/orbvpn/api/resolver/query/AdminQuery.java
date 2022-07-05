package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.AdminService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminQuery implements GraphQLQueryResolver {

    private final AdminService adminService;

    public Page<UserView> activeUsers(int page, int size) {
        return adminService.getActiveUsers(page, size);
    }

    public Page<UserView> inactiveUsers(int page, int size) {
        return adminService.getInactiveUsers(page, size);
    }

    public Page<UserView> allUsers(int page, int size, String param, String query) {
        return adminService.getAllUsers(page, size, param, query);
    }

    public int totalActiveUsers() {
        return adminService.getTotalActiveUsers();
    }
}
