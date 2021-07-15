package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.MoreLoginCount;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.reposiitory.MoreLoginCountRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoreLoginCountService {
  private final MoreLoginCountRepository moreLoginCountRepository;
  private final RadiusService radiusService;

  public void removeExpiredMoreLoginCount() {
    LocalDateTime now = LocalDateTime.now();

    List<MoreLoginCount> expired = moreLoginCountRepository.findByExpiresAtBefore(now);

    for (MoreLoginCount moreLoginCount : expired) {
      try {
        User user = moreLoginCount.getUser();
        if(user == null) {
          moreLoginCountRepository.delete(moreLoginCount);
          continue;
        }

        int number = moreLoginCount.getNumber();
        radiusService.subUserMoreLoginCount(user, number);
        moreLoginCountRepository.delete(moreLoginCount);
      } catch (Exception ex) {
        log.error("Couldn't remove more login {}", ex.getMessage());
      }
    }



  }
}
