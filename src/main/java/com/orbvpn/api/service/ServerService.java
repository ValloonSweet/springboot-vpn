package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ServerEditMapper;
import com.orbvpn.api.mapper.ServerViewMapper;
import com.orbvpn.api.reposiitory.ServerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServerService {
  private final ServerRepository serverRepository;
  private final ServerEditMapper serverEditMapper;
  private final ServerViewMapper serverViewMapper;

  private final RadiusService radiusService;

  public ServerView createServer(ServerEdit serverEdit) {
    log.info("Creating server with data {}", serverEdit);
    Server server = serverEditMapper.create(serverEdit);

    serverRepository.save(server);
    radiusService.createNas(server);
    ServerView serverView = serverViewMapper.toView(server);
    log.info("Created server {}", serverView);
    return serverView;
  }

  public ServerView editServer(int id, ServerEdit serverEdit) {
    log.info("Editing server with id {} with data {}", id, serverEdit);

    Server server = getServerById(id);
    String publicIp = server.getPublicIp();
    server = serverEditMapper.edit(server, serverEdit);
    serverRepository.save(server);
    radiusService.editNas(publicIp, server);

    ServerView serverView = serverViewMapper.toView(server);
    log.info("Edited server {}", serverView);
    return serverView;
  }

  public ServerView deleteServer(int id) {
    log.info("Deleting server with id {}", id);

    Server server = getServerById(id);
    serverRepository.delete(server);
    radiusService.deleteNas(server);

    ServerView serverView = serverViewMapper.toView(server);
    log.info("Deleted server {}", serverView);
    return serverView;
  }

  public ServerView getServer(int id) {
    Server server = getServerById(id);

    return serverViewMapper.toView(server);
  }

  public List<ServerView> getServers() {
    return serverRepository.findAll()
      .stream()
      .map(serverViewMapper::toView)
      .collect(Collectors.toList());
  }

  public Server getServerById(int id) {
    return serverRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(Server.class, id));
  }
}
