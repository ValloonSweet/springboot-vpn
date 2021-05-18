package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ResellerEditMapper;
import com.orbvpn.api.mapper.ResellerViewMapper;
import com.orbvpn.api.reposiitory.ResellerRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResellerService {

  private final ResellerRepository resellerRepository;
  private final ResellerViewMapper resellerViewMapper;
  private final ResellerEditMapper resellerEditMapper;
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final ServiceGroupService serviceGroupService;

  @Transactional
  public ResellerView createReseller(ResellerCreate resellerCreate) {
    log.info("Creating a reseller with data {} ", resellerCreate);
    Reseller reseller = resellerEditMapper.create(resellerCreate);
    User user = reseller.getUser();
    user.setRole(roleService.getByName(RoleName.RESELLER));
    user.setPassword(passwordEncoder.encode(resellerCreate.getPassword()));

    resellerRepository.save(reseller);
    ResellerView resellerView = resellerViewMapper.toView(reseller);

    log.info("Created a reseller with view {}", resellerView);
    return resellerView;
  }

  @Transactional
  public ResellerView getReseller(int id) {
    Reseller reseller = getResellerById(id);
    return resellerViewMapper.toView(reseller);
  }

  @Transactional
  public List<ResellerView> getEnabledResellers() {
    return resellerRepository.findAllByEnabled(true)
      .stream()
      .map(resellerViewMapper::toView)
      .collect(Collectors.toList());
  }

  @Transactional
  public ResellerView editReseller(int id, ResellerEdit resellerEdit) {
    log.info("Editing reseller with id {} with data {}", id, resellerEdit);
    Reseller reseller = getResellerById(id);
    resellerEditMapper.edit(reseller, resellerEdit);

    ResellerView resellerView = resellerViewMapper.toView(reseller);

    log.info("Edited reseller {}", resellerView);
    return resellerView;
  }

  @Transactional
  public ResellerView deleteReseller(int id) {
    log.info("Deleting reseller with id {}", id);
    Reseller reseller = getResellerById(id);

    User user = reseller.getUser();
    user.setEnabled(false);
    reseller.setEnabled(false);
    resellerRepository.save(reseller);

    return resellerViewMapper.toView(reseller);
  }

  @Transactional
  public ResellerView addResellerServiceGroup(int resellerId, int serviceGroupId) {
    Reseller reseller = getResellerById(resellerId);
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

    reseller.getServiceGroups().add(serviceGroup);

    resellerRepository.save(reseller);
    return resellerViewMapper.toView(reseller);
  }

  @Transactional
  public ResellerView removeResellerServiceGroup(int resellerId, int serviceGroupId) {
    Reseller reseller = getResellerById(resellerId);
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

    reseller.getServiceGroups().remove(serviceGroup);

    resellerRepository.save(reseller);
    return resellerViewMapper.toView(reseller);
  }

  //Only should be called when service group is removed
  public void removeServiceGroup(ServiceGroup serviceGroup) {
    List<Reseller> resellers = resellerRepository.findAll();
    resellers.forEach(reseller -> {
      reseller.getServiceGroups().remove(serviceGroup);
    });

    resellerRepository.saveAll(resellers);
  }

  public Reseller getResellerById(int id) {
    return resellerRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(Reseller.class, id));
  }
}
