package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.entity.Server;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServerEditMapper {
  Server edit(ServerEdit serverEdit);
}
