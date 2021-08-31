package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.RadAcct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RadAcctRepository extends JpaRepository<RadAcct, Integer> {
    long deleteByUsername(String username);
}
