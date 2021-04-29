package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.UserCreateMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserCreateMapper userCreateMapper;
  private final UserViewMapper userViewMapper;
  private final PasswordEncoder passwordEncoder;


  public UserView register(UserCreate userCreate) {
    User user = userCreateMapper.createEntity(userCreate);
    user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
    userRepository.save(user);
    return userViewMapper.toView(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username).orElseThrow();
  }
}
