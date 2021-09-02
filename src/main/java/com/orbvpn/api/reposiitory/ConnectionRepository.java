package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.RadAcct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<RadAcct, Integer> {
    @Query("SELECT radAcct from RadAcct radAcct, User user where radAcct.username = user.email and user.id = :userId and radAcct.acctstoptime is not null")
    List<RadAcct> findConnectionHistory(Integer userId);

    @Query("SELECT radAcct from RadAcct radAcct, User user where radAcct.username = user.email and user.id = :userId and radAcct.acctstoptime is not null")
    List<RadAcct> findOnlineSessions(Integer userId);
}
