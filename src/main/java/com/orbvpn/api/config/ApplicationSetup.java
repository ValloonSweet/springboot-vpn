package com.orbvpn.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationSetup {
    @Value("${application.default-timezone}")
    private String defaultTimeZone;

    @PostConstruct
    public void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
        log.info("Default time zone is set to " + TimeZone.getDefault().getDisplayName());
    }
}
