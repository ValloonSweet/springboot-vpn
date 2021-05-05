package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.domain.entity.Gateway;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.mapper.ServiceGroupViewMapper;
import com.orbvpn.api.reposiitory.GatewayRepository;
import com.orbvpn.api.reposiitory.ServiceGroupRepository;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceGroupService {

  private final ServiceGroupRepository serviceGroupRepository;
  private final GatewayRepository gatewayRepository;
  private final ServiceGroupViewMapper serviceGroupViewMapper;

  public ServiceGroupView createServiceGroup(ServiceGroupEdit serviceGroupEdit) {
    ServiceGroup serviceGroup = new ServiceGroup();

    serviceGroup.setName(serviceGroupEdit.getName());
    serviceGroup.setDescription(serviceGroupEdit.getDescription());

    List<Gateway> allById = gatewayRepository.findAllById(serviceGroupEdit.getGateways());
    serviceGroup.setGateways(allById);
    serviceGroupRepository.save(serviceGroup);

    return serviceGroupViewMapper.toView(serviceGroup);
  }

}
