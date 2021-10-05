package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.dto.DeviceIdInput;
import com.orbvpn.api.domain.entity.Device;
import com.orbvpn.api.domain.entity.RadAcct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RadAcctRepository extends JpaRepository<RadAcct, Integer> {
    long deleteByUsername(String username);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND" +
            " user.id = :userId AND radAcct.acctstoptime IS NOT NULL")
    List<RadAcct> findConnectionHistory(Integer userId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND " +
            "user.id = :userId AND radAcct.acctstoptime IS NULL")
    List<RadAcct> findOnlineSessions(Integer userId);

    RadAcct findByAcctsessionid(String acctsessionid);

    @Query("SELECT new com.orbvpn.api.domain.entity.Device(radAcct.connectinfo_start, MAX(radAcct.acctstarttime), " +
            "COUNT(radAcct.acctstarttime), COUNT(radAcct.acctstoptime)) " +
            "FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND user.id = :userId AND " +
            "radAcct.connectinfo_start IS NOT NULL GROUP BY radAcct.connectinfo_start")
    List<Device> getDevices(Integer userId);

    @Query("SELECT max(radAcct.acctsessionid) FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND " +
            "user.id = :userId AND radAcct.acctstoptime IS NULL AND  radAcct.connectinfo_start like  '%'|| (:deviceId) ||'%' ")
    String getOnlineSessionId(Integer userId, String deviceId);
}
