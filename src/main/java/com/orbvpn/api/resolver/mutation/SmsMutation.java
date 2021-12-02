package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.entity.SmsRequest;
import com.orbvpn.api.service.BirthdayService;
import com.orbvpn.api.service.SmsService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsMutation implements GraphQLMutationResolver {

    private SmsService smsService;
    private BirthdayService birthdayService;

    @RolesAllowed(ADMIN)
    public Boolean sendSms(SmsRequest smsRequest) {
        smsService.sendRequest(smsRequest);
        return true;
    }
}
