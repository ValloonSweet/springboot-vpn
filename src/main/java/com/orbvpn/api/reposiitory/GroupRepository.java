package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.ServiceGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
  Long deleteByServiceGroup(ServiceGroup serviceGroup);

  List<Group> findAllByServiceGroup(ServiceGroup serviceGroup);
}
