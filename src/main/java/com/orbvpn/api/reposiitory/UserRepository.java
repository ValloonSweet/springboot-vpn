package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  long countByCreatedAtAfter(LocalDateTime createdAt);

  Page<User> findAllByReseller(Reseller reseller, Pageable pageable);

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
}
