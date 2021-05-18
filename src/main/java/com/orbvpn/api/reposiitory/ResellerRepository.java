package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResellerRepository extends JpaRepository<Reseller, Integer> {
  List<Reseller> findAllByEnabled(boolean enabled);
}
