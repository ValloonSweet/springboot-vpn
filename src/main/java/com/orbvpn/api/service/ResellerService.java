package com.orbvpn.api.service;

import static java.time.temporal.ChronoUnit.DAYS;

import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerAddCredit;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.ResellerLevel;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.InternalException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ResellerEditMapper;
import com.orbvpn.api.mapper.ResellerViewMapper;
import com.orbvpn.api.reposiitory.ResellerAddCreditRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResellerService {

  @Setter
  private ServiceGroupService serviceGroupService;
  private final RoleService roleService;

  private final ResellerViewMapper resellerViewMapper;
  private final ResellerEditMapper resellerEditMapper;

  private final PasswordEncoder passwordEncoder;

  private final ResellerRepository resellerRepository;
  private final ResellerAddCreditRepository resellerAddCreditRepository;

  public Reseller getOwnerReseller() {
    return getResellerById(1);
  }

  public ResellerView createReseller(ResellerCreate resellerCreate) {
    log.info("Creating a reseller with data {} ", resellerCreate);

    Reseller reseller = resellerEditMapper.create(resellerCreate);
    reseller.setLevelSetDate(LocalDateTime.now());

    User user = reseller.getUser();
    user.setRole(roleService.getByName(RoleName.RESELLER));
    user.setPassword(passwordEncoder.encode(resellerCreate.getPassword()));

    resellerRepository.save(reseller);
    ResellerView resellerView = resellerViewMapper.toView(reseller);

    log.info("Created a reseller with view {}", resellerView);
    return resellerView;
  }

  public ResellerView getReseller(int id) {
    Reseller reseller = getResellerById(id);
    return resellerViewMapper.toView(reseller);
  }

  public List<ResellerView> getEnabledResellers() {
    return resellerRepository.findAllByEnabled(true)
      .stream()
      .map(resellerViewMapper::toView)
      .collect(Collectors.toList());
  }

  public BigDecimal getTotalResellersCredit() {
    BigDecimal total = BigDecimal.valueOf(0);
    List<Reseller> all = resellerRepository.findAll();
    for (Reseller reseller : all) {
      total = total.add(reseller.getCredit());
    }

    return total;
  }

  public ResellerView editReseller(int id, ResellerEdit resellerEdit) {
    log.info("Editing reseller with id {} with data {}", id, resellerEdit);

    Reseller reseller = getResellerById(id);
    resellerEditMapper.edit(reseller, resellerEdit);


    resellerRepository.save(reseller);
    ResellerView resellerView = resellerViewMapper.toView(reseller);

    log.info("Edited reseller {}", resellerView);
    return resellerView;
  }

  public ResellerView deleteReseller(int id) {
    log.info("Deleting reseller with id {}", id);
    Reseller reseller = getResellerById(id);

    User user = reseller.getUser();
    user.setEnabled(false);
    reseller.setEnabled(false);
    resellerRepository.save(reseller);

    return resellerViewMapper.toView(reseller);
  }

  public ResellerView addResellerServiceGroup(int resellerId, int serviceGroupId) {
    Reseller reseller = getResellerById(resellerId);
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

    reseller.getServiceGroups().add(serviceGroup);

    resellerRepository.save(reseller);
    return resellerViewMapper.toView(reseller);
  }

  public ResellerView removeResellerServiceGroup(int resellerId, int serviceGroupId) {
    Reseller reseller = getResellerById(resellerId);
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

    reseller.getServiceGroups().remove(serviceGroup);

    resellerRepository.save(reseller);
    return resellerViewMapper.toView(reseller);
  }

  public ResellerView setResellerLevel(int resellerId, ResellerLevel level) {
    log.info("Updating reseller {} level {}", resellerId, level);
    Reseller reseller = getResellerById(resellerId);

    reseller.setLevel(level);
    reseller.setLevelSetDate(LocalDateTime.now());
    resellerRepository.save(reseller);

    log.info("Reseller {} updated level {}", resellerId, level);

    return resellerViewMapper.toView(reseller);
  }

  public ResellerView addResellerCredit(int resellerId, BigDecimal credit) {
    log.info("Adding reseller {} credit {}", resellerId, credit);
    Reseller reseller = getResellerById(resellerId);

    BigDecimal curCredit = reseller.getCredit();
    reseller.setCredit(curCredit.add(credit));
    ResellerAddCredit resellerAddCredit = new ResellerAddCredit(reseller, credit);
    resellerRepository.save(reseller);
    resellerAddCreditRepository.save(resellerAddCredit);

    log.info("Added reseller {} credit {}", resellerId, credit);

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

  public Reseller getResellerByUser(User user) {
    return resellerRepository.findResellerByUser(user)
      .orElseThrow(()->new InternalException("Can't find reseller"));
  }

}
