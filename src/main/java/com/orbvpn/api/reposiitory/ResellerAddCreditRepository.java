package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.ResellerAddCredit;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResellerAddCreditRepository extends JpaRepository<ResellerAddCredit, Integer> {
  List<ResellerAddCredit> findAllByCreatedAtAfter(LocalDateTime createdAt);
}
