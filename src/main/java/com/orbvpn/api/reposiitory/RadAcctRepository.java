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

    @Query(value = "SELECT radacct.connectinfo_start as deviceInfo, radacct.acctstarttime as lastConnectionStartTime, " +
            "radacct.acctstoptime as lastConnectionStopTime, radacct.acctsessionid as lastSessionId, " +
            "srv.id as lastConnectedServerId, srv.country as lastConnectedServerCountry " +
            "from radacct, server srv " +
            "where radacct.nasipaddress = srv.private_ip AND " +
            "        radacct.radacctid in " +
            "        ((select max(radacct.radacctid) " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = user.email AND user.id = :userId " +
            "          GROUP BY radacct.connectinfo_start) " +
            "         union " +
            "         (select radacct.radacctid " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = user.email AND user.id = :userId AND " +
            "              radacct.acctstoptime IS NULL))", nativeQuery = true )
    List<Device> getDevices(Integer userId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = user.email AND  user.id = :userId AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%' ")
    RadAcct getOnlineSessionByUseridAndDeviceId(Integer userId, String deviceId);

    @Query("SELECT radAcct FROM RadAcct radAcct, User user WHERE radAcct.username = :username AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%'")
    RadAcct getOnlineSessionByUsernameAndDeviceId(String username, String deviceId);
}
