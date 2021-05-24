package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT;
import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT_NATIVE;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final UserViewMapper userViewMapper;

  public int getTotalActiveUsers() {
    return userRepository.getTotalActiveUsers();
  }

  public Page<UserView> getActiveUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

    return userRepository.findAllActiveUsers(pageable)
      .map(userViewMapper::toView);
  }

  public Page<UserView> getInactiveUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

    return userRepository.findAllNotActiveUsers(pageable)
      .map(userViewMapper::toView);
  }


}
