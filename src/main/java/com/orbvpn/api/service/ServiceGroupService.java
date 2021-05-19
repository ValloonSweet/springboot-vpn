package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ServiceGroupEditMapper;
import com.orbvpn.api.mapper.ServiceGroupViewMapper;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.ServiceGroupRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceGroupService {

  private final ServiceGroupRepository serviceGroupRepository;
  private final ServiceGroupEditMapper serviceGroupEditMapper;
  private final ServiceGroupViewMapper serviceGroupViewMapper;
  private final ResellerService resellerService;

  @PostConstruct
  public void init() {
    resellerService.setServiceGroupService(this);
  }

  @Transactional
  public ServiceGroupView createServiceGroup(ServiceGroupEdit serviceGroupEdit) {
    ServiceGroup serviceGroup = serviceGroupEditMapper.create(serviceGroupEdit);

    serviceGroupRepository.save(serviceGroup);

    return serviceGroupViewMapper.toView(serviceGroup);
  }

  @Transactional
  public ServiceGroupView editServiceGroup(int id, ServiceGroupEdit serviceGroupEdit) {
    ServiceGroup serviceGroup = getById(id);

    ServiceGroup serviceGroupEdited = serviceGroupEditMapper.edit(serviceGroup, serviceGroupEdit);

    serviceGroupRepository.save(serviceGroupEdited);

    return serviceGroupViewMapper.toView(serviceGroupEdited);
  }

  @Transactional
  public ServiceGroupView deleteServiceGroup(int id) {
    ServiceGroup serviceGroup = getById(id);

    serviceGroupRepository.delete(serviceGroup);
    resellerService.removeServiceGroup(serviceGroup);

    return serviceGroupViewMapper.toView(serviceGroup);
  }

  @Transactional
  public ServiceGroupView getServiceGroup(int id) {
    ServiceGroup serviceGroup = getById(id);

    return serviceGroupViewMapper.toView(serviceGroup);
  }

  @Transactional
  public List<ServiceGroupView> getAllServiceGroups() {
    return serviceGroupRepository.findAll()
      .stream()
      .map(serviceGroupViewMapper::toView)
      .collect(Collectors.toList());
  }

  public ServiceGroup getById(int id) {
    return serviceGroupRepository.findById(id)
      .orElseThrow(()->new NotFoundException("Service not found"));
  }

}
