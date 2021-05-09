package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.Role;
import com.orbvpn.api.reposiitory.UserRepository;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  public void createAdmin() {
    Optional<User> adminOptional = userRepository.findByEmail("admin@mail.com");
    if (adminOptional.isEmpty()) {
      User user = new User();
      user.setEmail("admin@mail.com");
      user.setPassword(passwordEncoder.encode("admin"));
      user.setFirstName("admin");
      user.setLastName("admin");
      user.setRole(Role.ADMIN);
      userRepository.save(user);
    }
  }
}
