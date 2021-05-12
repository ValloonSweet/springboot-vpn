package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserViewMapper {
  @Mapping(source = "role.name", target = "role")
  UserView toView(User user);
}
