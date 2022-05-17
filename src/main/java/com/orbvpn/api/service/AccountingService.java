package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.reposiitory.PaymentRepository;
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
  private final PaymentRepository paymentRepository;

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
    int monthPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentMonth);
    BigDecimal monthPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentMonth);
    int dayPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentDay);
    BigDecimal dayPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentDay);

    int monthRenewPurchaseCount = paymentRepository.getTotalRenewSubscriptionCount(currentMonth);
    BigDecimal monthRenewPurchase = paymentRepository.getTotalRenewSubscriptionPrice(currentMonth);
    int dayRenewPurchaseCount = paymentRepository.getTotalRenewSubscriptionCount(currentDay);
    BigDecimal dayRenewPurchase = paymentRepository.getTotalRenewSubscriptionPrice(currentDay);


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
