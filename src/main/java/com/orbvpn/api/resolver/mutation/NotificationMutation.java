package com.orbvpn.api.resolver.mutation;


import com.orbvpn.api.domain.dto.ScheduleEmailRequest;
import com.orbvpn.api.domain.dto.ScheduleEmailResponse;
import com.orbvpn.api.quartz.EmailJobScheduler;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMutation implements GraphQLMutationResolver {
    @Autowired
    private EmailJobScheduler emailJobScheduler;

    public Boolean scheduleEmail(ScheduleEmailRequest scheduleEmailRequest){
        ScheduleEmailResponse scheduleEmailResponse = emailJobScheduler.scheduleEmail(scheduleEmailRequest);
        log.info("email is scheduled. " + scheduleEmailResponse.toString());
        return scheduleEmailResponse.isSuccess();
    }
}
