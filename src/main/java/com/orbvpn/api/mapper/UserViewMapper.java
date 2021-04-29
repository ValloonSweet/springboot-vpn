package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserViewMapper {
  UserView toView(User user);
}
