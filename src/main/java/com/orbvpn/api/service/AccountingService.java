package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.reposiitory.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountingService {
  private final UserRepository userRepository;


  public AccountingView getAccounting() {
    AccountingView accountingView = new AccountingView();

    LocalDateTime dateTime = LocalDateTime.now();
    LocalDateTime currentDay = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 0, 0);
    LocalDateTime currentMonth = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0 ,0 );
    LocalDateTime currentYear = LocalDateTime.of(dateTime.getYear(), 1, 1, 0, 0);

    int totalUsers = (int) userRepository.count();
    int joinedByDay = (int) userRepository.countByCreatedAtAfter(currentDay);
    int joinedByMonth = (int) userRepository.countByCreatedAtAfter(currentMonth);
    int joinedByYear = (int) userRepository.countByCreatedAtAfter(currentYear);

    accountingView.setTotalUsers(totalUsers);
    accountingView.setJoinedByDay(joinedByDay);
    accountingView.setJoinedByMonth(joinedByMonth);
    accountingView.setJoinedByYear(joinedByYear);

    return accountingView;
  }
}
