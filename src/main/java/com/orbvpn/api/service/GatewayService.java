package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GatewayView;
import com.orbvpn.api.domain.entity.Gateway;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.mapper.GatewayViewMapper;
import com.orbvpn.api.reposiitory.GatewayRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayService {

  private final GatewayRepository gatewayRepository;
  private final GatewayViewMapper gatewayViewMapper;

  @PostConstruct
  public void init() {
    List<Gateway> all = gatewayRepository.findAll();
    if (all.isEmpty()) {
      Gateway visa = new Gateway();
      visa.setName(GatewayName.VISA);
      gatewayRepository.save(visa);
      Gateway masterCard = new Gateway();
      masterCard.setName(GatewayName.MASTERCARD);
      gatewayRepository.save(masterCard);
    }
  }

  public List<GatewayView> getAllGateways() {
    return gatewayRepository.findAll()
      .stream()
      .map(gatewayViewMapper::toView)
      .collect(Collectors.toList());
  }
}
