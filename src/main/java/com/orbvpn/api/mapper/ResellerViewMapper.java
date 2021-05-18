package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.Reseller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = ServiceGroupViewMapper.class)
public interface ResellerViewMapper {
  @Mappings({
    @Mapping(source = "user.email", target = "email"),
    @Mapping(source = "user.firstName", target = "firstName"),
    @Mapping(source = "user.lastName", target = "lastName")
  })
  ResellerView toView(Reseller reseller);
}
