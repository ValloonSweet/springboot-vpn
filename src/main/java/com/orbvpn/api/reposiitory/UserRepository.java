package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  long countByCreatedAtAfter(LocalDateTime createdAt);
}
