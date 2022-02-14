package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.service.UserService;
import com.orbvpn.api.service.UserSubscriptionService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
@Validated
public class SubscriptionMutation implements GraphQLMutationResolver {

    private final UserSubscriptionService userSubscriptionService;
    private final UserService userService;

    @RolesAllowed(ADMIN)
    public UserSubscriptionView renewWithoutGroup(String username, int day) {

        User user = userService.getUserByUsername(username);
        return userSubscriptionService.renewWithDayCount(user, day);
    }

}
