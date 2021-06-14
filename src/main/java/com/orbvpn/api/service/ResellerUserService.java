package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT;

import com.orbvpn.api.domain.dto.ResellerUserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.InsufficientFundsException;
import com.orbvpn.api.mapper.ResellerUserCreateMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
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

  private final UserService userService;
  private final RoleService roleService;
  private final GroupService groupService;
  private final UserSubscriptionService userSubscriptionService;

  private final UserRepository userRepository;
  private final ResellerRepository resellerRepository;


  public UserView createUser(ResellerUserCreate resellerUserCreate) {
    log.info("Creating user");
    User creator = userService.getUser();
    Reseller reseller = creator.getReseller();
    Group group = groupService.getById(resellerUserCreate.getGroupId());

    Optional<User> userEntityOptional = userRepository.findByEmail(resellerUserCreate.getEmail());
    if (userEntityOptional.isPresent()) {
      throw new BadRequestException("User with specified email exists");
    }

    User user = resellerUserCreateMapper.create(resellerUserCreate);
    user.setUsername(resellerUserCreate.getEmail());
    user.setPassword(passwordEncoder.encode(resellerUserCreate.getPassword()));
    user.setRadAccess(resellerUserCreate.getPassword());
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(reseller);

    userRepository.save(user);
    createResellerUserSubscription(user, group);

    UserView userView = userViewMapper.toView(user);
    log.info("Created user");

    return userView;
  }

  @Transactional
  public UserSubscription createResellerUserSubscription(User user, Group group) {
    Reseller reseller = user.getReseller();

    BigDecimal credit = reseller.getCredit();
    BigDecimal price = calculatePrice(reseller, group);
    if (credit.compareTo(price) < 0) {
      throw new InsufficientFundsException();
    }
    reseller.setCredit(credit.subtract(price));
    resellerRepository.save(reseller);

    String paymentId = UUID.randomUUID().toString();
    UserSubscription userSubscription = userSubscriptionService
      .createUserSubscription(user, group, PaymentType.RESELLER_CREDIT, PaymentStatus.PENDING,
        paymentId);
    userSubscriptionService.fullFillSubscription(userSubscription);

    return userSubscription;
  }


  public BigDecimal calculatePrice(Reseller reseller, Group group) {
    ResellerLevel level = reseller.getLevel();
    if (level.getName() == ResellerLevelName.OWNER) {
      return BigDecimal.ZERO;
    }

    BigDecimal price = group.getPrice();
    BigDecimal discount = price.multiply(level.getDiscountPercent()).divide(new BigDecimal(100));

    return price.subtract(discount);
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
