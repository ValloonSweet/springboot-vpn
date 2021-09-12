package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByOauthId(String oauthId);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  long countByCreatedAtAfter(LocalDateTime createdAt);

  Page<User> findAllByReseller(Reseller reseller, Pageable pageable);

  @Query("SELECT user from User user where user.id not in (select sub.user.id from UserSubscription sub where sub.expiresAt > :dateTime)")
  Page<User> findAllExpiredUsers(LocalDateTime dateTime, Pageable pageable);

  @Query("SELECT user from User user where user.reseller = :reseller and user.id not in (select sub.user.id from UserSubscription sub where sub.expiresAt > :dateTime)")
  Page<User> findAllResellerExpiredUsers(Reseller reseller, LocalDateTime dateTime, Pageable pageable);

  @Query(
    value = "SELECT count(*) FROM user where role_id=3 and email in (select username from radacct where acctstoptime IS NULL)",
    nativeQuery = true)
  int getTotalActiveUsers();

  @Query(
    value = "SELECT * FROM user where role_id=3 and email in (select username from radacct where acctstoptime IS NULL)",
    countQuery = "SELECT count(*) FROM user where role_id=3 and email in (select username from radacct where acctstoptime IS NULL)",
    nativeQuery = true)
  Page<User> findAllActiveUsers(Pageable pageable);

  @Query(
    value = "SELECT * FROM user where role_id=3 and email not in (select username from radacct where acctstoptime IS NULL)",
    countQuery = "SELECT count(*) FROM user where role_id=3 and email not in (select username from radacct where acctstoptime IS NULL)",
    nativeQuery = true)
  Page<User> findAllNotActiveUsers(Pageable pageable);

  @Query(value = "select distinct(connectinfo_start) from radacct where username = :username", nativeQuery = true)
  List<String> findAllUserDevices(String username);

  @Query(value = "select distinct(connectinfo_start) from radacct where username = :username and acctstoptime is null", nativeQuery = true)
  List<String> findAllActiveUserDevices(String username);

  @Query(value = "select user.* " +
          "FROM user " +
          "         LEFT JOIN radacct radAcct ON radAcct.username = user.email " +
          "         LEFT JOIN user_subscription subs ON user.id = subs.user_id " +
          "         LEFT JOIN group_app g ON subs.group_id = g.id " +
          "         LEFT JOIN service_group sg ON g.service_group_id = sg.id " +
          "         LEFT JOIN role r ON user.role_id = r.id " +
          "         LEFT JOIN server ON radAcct.nasipaddress = server.private_ip " +
          "WHERE radAcct.acctstoptime IS NULL " +
          "  AND (:roleId IS NULL OR r.id = :roleId) " +
          "  AND (:groupId IS NULL OR g.id = :groupId) " +
          "  AND (:serverId IS NULL OR server.id = :serverId) " +
          "  AND (:serviceGroupId IS NULL OR :serviceGroupId = sg.id)",
          countQuery = "SELECT count(user.email) " +
                  "FROM user " +
                  "         LEFT JOIN radacct radAcct ON radAcct.username = user.email " +
                  "         LEFT JOIN user_subscription subs ON user.id = subs.user_id " +
                  "         LEFT JOIN group_app g ON subs.group_id = g.id " +
                  "         LEFT JOIN service_group sg ON g.service_group_id = sg.id " +
                  "         LEFT JOIN role r ON user.role_id = r.id " +
                  "         LEFT JOIN server ON radAcct.nasipaddress = server.private_ip " +
                  "WHERE radAcct.acctstoptime IS NULL " +
                  "  AND (:roleId IS NULL OR r.id = :roleId) " +
                  "  AND (:groupId IS NULL OR g.id = :groupId) " +
                  "  AND (:serverId IS NULL OR server.id = :serverId) " +
                  "  AND (:serviceGroupId IS NULL OR :serviceGroupId = sg.id)",
          nativeQuery = true)
  Page<User> findOnlineUsers(Pageable pageable, Integer serverId, Integer groupId, Integer roleId, Integer serviceGroupId);

}
