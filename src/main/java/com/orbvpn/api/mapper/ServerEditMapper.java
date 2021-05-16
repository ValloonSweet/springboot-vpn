package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.entity.Server;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServerEditMapper {
  Server create(ServerEdit serverEdit);

  Server edit(@MappingTarget Server server, ServerEdit serverEdit);
}
