package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResellerRepository extends JpaRepository<Reseller, Integer> {
  List<Reseller> findAllByEnabled(boolean enabled);

  Optional<Reseller> findResellerByUser(User user);

  List<Reseller> findByLevelSetDateBefore(LocalDateTime levelSetDate);
}
