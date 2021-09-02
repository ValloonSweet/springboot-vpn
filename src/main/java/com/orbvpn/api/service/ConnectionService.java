package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.domain.entity.RadAcct;
import com.orbvpn.api.mapper.ConnectionMapper;
import com.orbvpn.api.reposiitory.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ConnectionMapper connectionMapper;

    public List<ConnectionHistoryView> getConnectionHistory(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findConnectionHistory(userId);
        List<ConnectionHistoryView> connectionHistoryViews = radaccts.stream()
                .map(connectionMapper::connectionHistoryView)
                .collect(Collectors.toList());
        return connectionHistoryViews;
    }

    public List<OnlineSessionView> getOnlineSessions(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findOnlineSessions(userId);
        List<OnlineSessionView> connectionHistoryViews = radaccts.stream()
                .map(connectionMapper::onlineSessionView)
                .collect(Collectors.toList());
        return connectionHistoryViews;
    }
}
