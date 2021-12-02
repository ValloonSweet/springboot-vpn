package com.orbvpn.api.service;

import com.orbvpn.api.config.sms.TwilioConfig;
import com.orbvpn.api.domain.entity.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {
    @Autowired
    private TwilioConfig twilioConfig;

    public void sendRequest(SmsRequest smsRequest) {

        //to, from , message
        MessageCreator creator = Message.creator(
                new PhoneNumber(smsRequest.getPhoneNumber()),
                new PhoneNumber(twilioConfig.getTrialNumber()),
                smsRequest.getMessage()
        );

        creator.create(); //sends the message
        log.info("Sms sent to --- [ " + smsRequest.getPhoneNumber() + " ] -----");
    }
}
