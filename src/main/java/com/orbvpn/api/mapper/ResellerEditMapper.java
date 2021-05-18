package com.orbvpn.api.mapper;


import com.orbvpn.api.domain.dto.ResellerCreate;
import com.orbvpn.api.domain.dto.ResellerEdit;
import com.orbvpn.api.domain.entity.Reseller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResellerEditMapper {
  @Mappings({
    @Mapping(source = "email", target = "user.email"),
    @Mapping(source = "firstName", target = "user.firstName"),
    @Mapping(source = "lastName", target = "user.lastName"),
  })
  Reseller create(ResellerCreate resellerCreate);

  @Mappings({
    @Mapping(source = "email", target = "user.email"),
    @Mapping(source = "firstName", target = "user.firstName"),
    @Mapping(source = "lastName", target = "user.lastName"),
  })
  Reseller edit(@MappingTarget Reseller reseller, ResellerEdit resellerEdit);
}
