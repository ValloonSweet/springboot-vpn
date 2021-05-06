package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {

}
