package com.orbvpn.api.reposiitory;

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

    @Query(value = "SELECT radAcct.connectinfo_start as deviceInfo, radAcct.acctstarttime as lastConnectionStartTime, " +
            "radAcct.acctstoptime as lastConnectionStopTime, radAcct.acctsessionid as lastSessionId, " +
            "srv.id as lastConnectedServerId, srv.country as lastConnectedServerCountry " +
            "from RadAcct radAcct, server srv " +
            "where radAcct.nasipaddress = srv.private_ip AND " +
            "        radAcct.radacctid in " +
            "        ((select max(radAcct.radacctid) " +
            "          FROM RadAcct radAcct, User user " +
            "          WHERE radAcct.username = user.email AND user.id = :userId " +
            "          GROUP BY radAcct.connectinfo_start) " +
            "         union " +
            "         (select radAcct.radacctid " +
            "          FROM RadAcct radAcct, User user " +
            "          WHERE radAcct.username = user.email AND user.id = :userId AND " +
            "              radAcct.acctstoptime IS NULL))", nativeQuery = true )
    List<Device> getDevices(Integer userId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND  user.id = :userId AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%' ")
    RadAcct getOnlineSessionByUseridAndDeviceId(Integer userId, String deviceId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = :username AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%'")
    RadAcct getOnlineSessionByUsernameAndDeviceId(String username, String deviceId);
}
