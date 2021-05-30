package com.orbvpn.api.service;

import static java.time.temporal.ChronoUnit.DAYS;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerAddCredit;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.ResellerLevel;
import com.orbvpn.api.reposiitory.ResellerAddCreditRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerLevelService {

  private static final BigDecimal MONTH_CREDIT_COEF = BigDecimal.ONE;
  private static final BigDecimal MONTH_CREDIT_MAX = BigDecimal.valueOf(30);
  private static final BigDecimal CURRENT_CREDIT_COEF = BigDecimal.ONE;
  private static final BigDecimal CURRENT_CREDIT_MAX = BigDecimal.valueOf(20);
  private static final BigDecimal ACTIVE_SUBSCRIPTION_COEF = BigDecimal.ONE;
  private static final BigDecimal ACTIVE_SUBSCRIPTION_MAX = BigDecimal.valueOf(15);
  private static final BigDecimal MEMBERSHIP_DURATION_COEF = BigDecimal.ONE;
  private static final BigDecimal MEMBERSHIP_DURATION_MAX = BigDecimal.valueOf(5);
  private static final BigDecimal DEPOSIT_INTERVAL_COEF = BigDecimal.ONE;
  private static final BigDecimal DEPOSIT_INTERVAL_MAX = BigDecimal.valueOf(15);
  private static final BigDecimal TOTAL_SALE_COEF = BigDecimal.ONE;
  private static final BigDecimal TOTAL_SALE_MAX = BigDecimal.valueOf(10);
  private static final BigDecimal MONTH_SALE_COEF = BigDecimal.ONE;
  private static final BigDecimal MONTH_SALE_MAX = BigDecimal.valueOf(5);

  private final UserSubscriptionRepository userSubscriptionRepository;
  private final ResellerRepository resellerRepository;
  private final ResellerAddCreditRepository resellerAddCreditRepository;

  public void updateResellersLevel() {
    LocalDateTime monthBefore = LocalDateTime.now().minusMonths(1L);
    List<Reseller> resellers = resellerRepository.findByLevelSetDateBefore(monthBefore);

    for (Reseller reseller : resellers) {
      ResellerLevel level = getResellerLevel(reseller);
      reseller.setLevel(level);
      reseller.setLevelSetDate(LocalDateTime.now());
    }
    resellerRepository.saveAll(resellers);
  }

  private ResellerLevel getResellerLevel(Reseller reseller) {
    if (reseller.getLevel() == ResellerLevel.OWNER) {
      return ResellerLevel.OWNER;
    }

    BigDecimal totalScore = BigDecimal.ZERO;

    LocalDateTime lastSetDate = reseller.getLevelSetDate();
    LocalDateTime now = LocalDateTime.now();
    List<ResellerAddCredit> monthCredits = resellerAddCreditRepository
      .findAllByCreatedAtAfter(lastSetDate);

    BigDecimal monthCreditTotal = BigDecimal.ZERO;
    for (ResellerAddCredit monthCredit : monthCredits) {
      monthCreditTotal = monthCreditTotal.add(monthCredit.getCredit());
    }

    BigDecimal currentCredit = reseller.getCredit();

    BigDecimal activeSubscriptions = BigDecimal.valueOf(userSubscriptionRepository
      .countByPaymentStatusAndExpiresAtAfterAndUser_Reseller(PaymentStatus.SUCCEEDED,
        LocalDateTime.now(), reseller));

    BigDecimal membershipDuration = BigDecimal.valueOf(DAYS.between(reseller.getCreatedAt(), now));

    BigDecimal depositIntervals = BigDecimal
      .valueOf(30 / (monthCredits.size() == 0 ? 1 : monthCredits.size() + 1));

    BigDecimal totalSales = userSubscriptionRepository
      .getTotalSubscriptionPrice(reseller.getCreatedAt(), PaymentStatus.SUCCEEDED);

    BigDecimal totalMonthSales = userSubscriptionRepository
      .getTotalSubscriptionPrice(lastSetDate, PaymentStatus.SUCCEEDED);

    totalScore = totalScore
      .add(calculateScore(monthCreditTotal, MONTH_CREDIT_COEF, MONTH_CREDIT_MAX));
    totalScore = totalScore
      .add(calculateScore(currentCredit, CURRENT_CREDIT_COEF, CURRENT_CREDIT_MAX));
    totalScore = totalScore
      .add(calculateScore(activeSubscriptions, ACTIVE_SUBSCRIPTION_COEF, ACTIVE_SUBSCRIPTION_MAX));
    totalScore = totalScore
      .add(calculateScore(membershipDuration, MEMBERSHIP_DURATION_COEF, MEMBERSHIP_DURATION_MAX));
    totalScore = totalScore
      .add(calculateScore(depositIntervals, DEPOSIT_INTERVAL_COEF, DEPOSIT_INTERVAL_MAX));
    totalScore = totalScore.add(calculateScore(totalSales, TOTAL_SALE_COEF, TOTAL_SALE_MAX));
    totalScore = totalScore.add(calculateScore(totalMonthSales, MONTH_SALE_COEF, MONTH_SALE_MAX));

    ResellerLevel level = ResellerLevel.STARTER;
    ResellerLevel[] resellerLevelValues = ResellerLevel.values();
    // Skip ResellerLevel.OWNER
    for (int i = 0; i < resellerLevelValues.length - 1; ++i) {
      ResellerLevel curLevel = resellerLevelValues[i];
      if (totalScore.compareTo(curLevel.getMinScore()) > 0) {
        level = curLevel;
      } else {
        break;
      }
    }

    return level;
  }

  public BigDecimal calculateScore(BigDecimal value, BigDecimal coef, BigDecimal max) {
    BigDecimal score = value.multiply(coef);
    return score.compareTo(max) < 0 ? score : max;
  }
}
