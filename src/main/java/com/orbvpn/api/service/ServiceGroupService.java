package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.domain.entity.Gateway;
import com.orbvpn.api.domain.entity.Geolocation;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ServiceGroupViewMapper;
import com.orbvpn.api.reposiitory.GatewayRepository;
import com.orbvpn.api.reposiitory.GeolocationRepository;
import com.orbvpn.api.reposiitory.ServiceGroupRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceGroupService {

  private final ServiceGroupRepository serviceGroupRepository;
  private final GatewayRepository gatewayRepository;
  private final GeolocationRepository geolocationRepository;
  private final ServiceGroupViewMapper serviceGroupViewMapper;

  @Transactional
  public ServiceGroupView createServiceGroup(ServiceGroupEdit serviceGroupEdit) {
    ServiceGroup serviceGroup = new ServiceGroup();

    serviceGroup.setName(serviceGroupEdit.getName());
    serviceGroup.setDescription(serviceGroupEdit.getDescription());

    List<Gateway> allById = gatewayRepository.findAllById(serviceGroupEdit.getGateways());
    serviceGroup.setGateways(allById);

    List<Geolocation> allowedGeolocations = geolocationRepository
      .findAllById(serviceGroupEdit.getAllowedGeolocations());
    serviceGroup.setAllowedGeolocations(allowedGeolocations);

    List<Geolocation> disallowedGeolocations = geolocationRepository
      .findAllById(serviceGroupEdit.getDisAllowedGeolocations());
    serviceGroup.setDisAllowedGeolocations(disallowedGeolocations);

    serviceGroupRepository.save(serviceGroup);

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
