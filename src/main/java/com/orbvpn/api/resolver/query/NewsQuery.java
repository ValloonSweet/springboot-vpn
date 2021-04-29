package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.NewsView;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsQuery implements GraphQLQueryResolver {
  List<NewsView> getNews(int count, int offset) {
    return List.of(NewsView.builder().id(0).title("default").description("my description").build());
  }
}
