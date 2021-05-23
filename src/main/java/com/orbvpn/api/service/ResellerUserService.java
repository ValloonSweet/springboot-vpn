package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT;

import com.orbvpn.api.domain.dto.ResellerUserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.mapper.ResellerUserCreateMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResellerUserService {

  private final ResellerUserCreateMapper resellerUserCreateMapper;
  private final UserViewMapper userViewMapper;

  private final PasswordEncoder passwordEncoder;

  private final ResellerService resellerService;
  private final UserService userService;
  private final RoleService roleService;
  private final GroupService groupService;

  private final UserRepository userRepository;

  //TODO reduce credit from reseller
  public UserView createUser(ResellerUserCreate resellerUserCreate) {
    log.info("Creating user");
    User creator = userService.getUser();
    Reseller reseller = creator.getReseller();
    Group group = groupService.getById(resellerUserCreate.getGroupId());

    User user = resellerUserCreateMapper.create(resellerUserCreate);
    user.setPassword(passwordEncoder.encode(resellerUserCreate.getPassword()));
    user.setRadAccess(UUID.randomUUID().toString());
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(reseller);

    userRepository.save(user);

    UserView userView = userViewMapper.toView(user);
    log.info("Created user");

    return userView;
  }

  public UserView deleteUser(int id) {
    log.info("Deleting user with id {}", id);

    User user = userService.getUserById(id);
    checkResellerUserAccess(user);
    userRepository.delete(user);
    UserView userView = userViewMapper.toView(user);

    log.info("Deleted user with id {}", id);
    return userView;
  }

  public UserView getUser(int id) {
    User user = userService.getUserById(id);
    checkResellerUserAccess(user);
    return userViewMapper.toView(user);
  }

  public Page<UserView> getUsers(int page, int size) {
    User accessorUser = userService.getUser();
    Reseller reseller = accessorUser.getReseller();
    Role accessorRole = accessorUser.getRole();
    Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT));

    Page<User> queryResult;
    if (accessorRole.getName() == RoleName.ADMIN) {
      queryResult = userRepository.findAll(pageable);
    } else {
      queryResult = userRepository.findAllByReseller(reseller, pageable);
    }

    return queryResult.map(userViewMapper::toView);
  }

  public void checkResellerUserAccess(User user) {
    User accessorUser = userService.getUser();
    Reseller reseller = accessorUser.getReseller();
    Role accessorRole = accessorUser.getRole();
    if (accessorRole.getName() != RoleName.ADMIN && user.getReseller().getId() != reseller
      .getId()) {
      throw new AccessDeniedException("Can't access user");
    }
  }
}
