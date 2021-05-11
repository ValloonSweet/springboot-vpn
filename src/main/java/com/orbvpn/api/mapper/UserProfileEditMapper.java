package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserProfileEdit;
import com.orbvpn.api.domain.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileEditMapper {
  UserProfile edit(@MappingTarget UserProfile userProfile, UserProfileEdit userProfileEdit);
}
