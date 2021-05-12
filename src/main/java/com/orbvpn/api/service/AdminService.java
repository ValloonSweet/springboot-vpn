package com.orbvpn.api.service;

import com.orbvpn.api.reposiitory.UserRepository;
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


}
