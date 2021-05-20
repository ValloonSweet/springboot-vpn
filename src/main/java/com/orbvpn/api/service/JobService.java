package com.orbvpn.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private static final int HOUR_RATE = 60 * 60 * 1000;
  private final HelpCenterService helpCenterService;

  @Scheduled(fixedRate = HOUR_RATE)
  public void removeOldTickets() {
    helpCenterService.removeOldTickets();
  }
}
