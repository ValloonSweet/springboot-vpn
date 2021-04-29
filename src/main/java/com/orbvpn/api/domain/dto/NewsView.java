package com.orbvpn.api.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NewsView {
  private final int id;
  private final String title;
  private final String description;
}
