package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.NewsView;
import com.orbvpn.api.domain.entity.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleViewMapper.class)
public interface NewsViewMapper {

  NewsView toView(News news);
}
