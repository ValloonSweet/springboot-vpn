package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.RadAcct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<RadAcct, Integer> {
    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND user.id = :userId AND radAcct.acctstoptime IS NOT NULL")
    List<RadAcct> findConnectionHistory(Integer userId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND user.id = :userId AND radAcct.acctstoptime IS NULL")
    List<RadAcct> findOnlineSessions(Integer userId);

    RadAcct findByAcctsessionid(String acctsessionid);
}
