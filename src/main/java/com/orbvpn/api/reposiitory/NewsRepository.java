package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Integer> {

}
