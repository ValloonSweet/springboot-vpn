package com.orbvpn.api.mapper;


import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.reposiitory.ResellerLevelRepository;
import com.orbvpn.api.service.ResellerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ResellerEditMapper {
  @Autowired
  private ResellerLevelRepository resellerLevelRepository;

  @Mappings({
    @Mapping(source = "email", target = "user.username"),
    @Mapping(source = "email", target = "user.email"),
    @Mapping(source = "firstName", target = "user.firstName"),
    @Mapping(source = "lastName", target = "user.lastName"),
  })
  public abstract Reseller create(ResellerCreate resellerCreate);

  @Mappings({
    @Mapping(source = "email", target = "user.username"),
    @Mapping(source = "email", target = "user.email"),
    @Mapping(source = "firstName", target = "user.firstName"),
    @Mapping(source = "lastName", target = "user.lastName"),
  })
  public abstract Reseller edit(@MappingTarget Reseller reseller, ResellerEdit resellerEdit);

  ResellerLevel toLevel(ResellerLevelName levelName) {
    return resellerLevelRepository.getByName(levelName);
  }
}
