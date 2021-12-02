package com.orbvpn.api.config.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    //twilio gives us account_sid , auth_token and trial number
    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.number}")
    private String trialNumber;

    public TwilioConfig() {

    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getTrialNumber() {
        return trialNumber;
    }

}
