package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.NewsEdit;
import com.orbvpn.api.domain.entity.News;
import com.orbvpn.api.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleService.class})
public interface NewsEditMapper {

  News create(NewsEdit newsEdit);

  News edit(@MappingTarget News news, NewsEdit newsEdit);
}
