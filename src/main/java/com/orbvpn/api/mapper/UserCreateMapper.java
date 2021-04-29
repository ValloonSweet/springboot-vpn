package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCreateMapper {
  User createEntity(UserCreate userCreate);
}
