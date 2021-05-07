package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.mapper.ServerEditMapper;
import com.orbvpn.api.mapper.ServerViewMapper;
import com.orbvpn.api.reposiitory.ServerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerService {
  private final ServerRepository serverRepository;
  private final ServerEditMapper serverEditMapper;
  private final ServerViewMapper serverViewMapper;

  public ServerView createServer(ServerEdit serverEdit) {
    Server server = serverEditMapper.edit(serverEdit);

    serverRepository.save(server);

    return serverViewMapper.toView(server);
  }

  public List<ServerView> getServers() {
    return serverRepository.findAll()
      .stream()
      .map(serverViewMapper::toView)
      .collect(Collectors.toList());
  }
}
