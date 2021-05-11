package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

  Optional<UserProfile> findByUser(User user);
}
