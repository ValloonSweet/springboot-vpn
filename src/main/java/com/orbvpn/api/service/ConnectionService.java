package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.RadAcct;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.ConnectionMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.ConnectionRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final ConnectionMapper connectionMapper;
    private final UserViewMapper userViewMapper;

    public List<ConnectionHistoryView> getConnectionHistory(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findConnectionHistory(userId);
        return radaccts.stream()
                .map(connectionMapper::connectionHistoryView)
                .collect(Collectors.toList());
    }

    public List<OnlineSessionView> getOnlineSessions(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findOnlineSessions(userId);
        return radaccts.stream()
                .map(connectionMapper::onlineSessionView)
                .collect(Collectors.toList());
    }

    public Page<UserView> getOnlineUsers(Integer page, Integer size, Integer serverId, Integer groupId, Integer roleId, Integer serviceGroupId) {
        Pageable pageable = PageRequest.of(page, size);//, Sort.by(DEFAULT_SORT_NATIVE)
        Page<User> users = userRepository.findOnlineUsers(pageable, serverId, groupId, roleId, serviceGroupId);
        return users.map(userViewMapper::toView);
    }
}
