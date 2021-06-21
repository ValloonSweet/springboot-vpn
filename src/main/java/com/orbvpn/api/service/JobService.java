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
  private final ResellerLevelService resellerLevelService;
  private final RenewUserSubscriptionService renewUserSubscriptionService;

  @Scheduled(fixedRate = HOUR_RATE)
  public void removeOldTickets() {
    log.info("Starting job for removing old tickets");
    helpCenterService.removeOldTickets();
    log.info("Removing old tickets job is finished");
  }

  @Scheduled(fixedRate = HOUR_RATE)
  public void updateResellerLevels() {
    log.info("Starting job for updating reseller level");
    resellerLevelService.updateResellersLevel();
    log.info("Finished job for updating reseller level");
  }

  @Scheduled(fixedRate = HOUR_RATE)
  public void renewSubscriptions() {
    log.info("Started renewing user subscriptions");
    renewUserSubscriptionService.renewSubscriptions();
    log.info("Finished job renewing subscriptions");
  }

}
