package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountingService {
  private final UserRepository userRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;

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
    int monthPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentMonth, PaymentStatus.SUCCEEDED);
    BigDecimal monthPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentMonth, PaymentStatus.SUCCEEDED);
    int dayPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentDay, PaymentStatus.SUCCEEDED);
    BigDecimal dayPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentDay, PaymentStatus.SUCCEEDED);

    int monthRenewPurchaseCount = userSubscriptionRepository.countTotalRenewSubscriptionCount(currentMonth, PaymentStatus.SUCCEEDED);
    BigDecimal monthRenewPurchase = userSubscriptionRepository.getTotalRenewSubscriptionPrice(currentMonth, PaymentStatus.SUCCEEDED);
    int dayRenewPurchaseCount = userSubscriptionRepository.countTotalRenewSubscriptionCount(currentDay, PaymentStatus.SUCCEEDED);
    BigDecimal dayRenewPurchase = userSubscriptionRepository.getTotalRenewSubscriptionPrice(currentDay, PaymentStatus.SUCCEEDED);


    accountingView.setTotalUsers(totalUsers);
    accountingView.setJoinedByDay(joinedByDay);
    accountingView.setJoinedByMonth(joinedByMonth);
    accountingView.setJoinedByYear(joinedByYear);
    accountingView.setMonthPurchaseCount(monthPurchaseCount);
    accountingView.setMonthPurchase(monthPurchase);
    accountingView.setDayPurchaseCount(dayPurchaseCount);
    accountingView.setDayPurchase(dayPurchase);
    accountingView.setMonthRenewPurchaseCount(monthRenewPurchaseCount);
    accountingView.setMonthRenewPurchase(monthRenewPurchase);
    accountingView.setDayRenewPurchaseCount(dayRenewPurchaseCount);
    accountingView.setDayRenewPurchase(dayRenewPurchase);

    return accountingView;
  }
}
