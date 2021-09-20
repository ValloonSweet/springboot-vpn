package com.orbvpn.api.service;

import com.orbvpn.api.reposiitory.RadAcctRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private static final int HOUR_RATE = 60 * 60 * 1000;
  private static final int FIVE_MIN_RATE = 5 * 60 * 1000;

  private final HelpCenterService helpCenterService;
  private final ResellerLevelService resellerLevelService;
  private final RenewUserSubscriptionService renewUserSubscriptionService;
  private final MoreLoginCountService moreLoginCountService;
  private final RadAcctRepository radAcctRepository;

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

  @Scheduled(fixedRate = HOUR_RATE)
  public void removeExpiredMoreLoginCount() {
    log.info("Started removing expired moore login count");
    moreLoginCountService.removeExpiredMoreLoginCount();
    log.info("Finished removing expired more login count");
  }

  @Scheduled(fixedRate = FIVE_MIN_RATE)
  public void removeAllRadacctTemporarily() {
    //https://freeradius-users.freeradius.narkive.com/5ULrgWHb/user-freezing
    log.info("Started removing all radacct records");
    radAcctRepository.deleteAllInBatch();
    moreLoginCountService.removeExpiredMoreLoginCount();
    log.info("Finished removing all radacct records");
  }
}
