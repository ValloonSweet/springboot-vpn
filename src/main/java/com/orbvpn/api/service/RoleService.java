package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.reposiitory.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository roleRepository;

  public List<Role> findAllById(List<Integer> id) {
    return roleRepository.findAllById(id);
  }
}
