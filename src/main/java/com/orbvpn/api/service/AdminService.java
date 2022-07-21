package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT_NATIVE;

import java.io.Console;
import java.util.List;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserViewMapper userViewMapper;

    public int getTotalActiveUsers() {
        return userRepository.getTotalActiveUsers();
    }

    public Page<UserView> getActiveUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

        return userRepository.findAllActiveUsers(pageable)
                .map(userViewMapper::toView);
    }

    public Page<UserView> getInactiveUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

        return userRepository.findAllNotActiveUsers(pageable)
                .map(userViewMapper::toView);
    }

    public Page<UserView> getAllUsers(int page, int size, String param, String query) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        if (param == null) {

            return userRepository.findByRoleId(3, pageable)
                    .map(userViewMapper::toView);
        }

        else if (param.equals("email"))
            return userRepository.findAllUsers(query, pageable)
                    .map(userViewMapper::toView);
        else
            return userRepository.findAll(pageable)
                    .map(userViewMapper::toView);
    }

}
