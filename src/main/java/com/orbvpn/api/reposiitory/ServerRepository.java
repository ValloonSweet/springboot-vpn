package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Integer> {
    Server findByPrivateIp(String privateIp);
}
