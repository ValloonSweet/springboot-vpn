package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.domain.entity.Group;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupViewMapper {
  GroupView toView(Group group);
}
