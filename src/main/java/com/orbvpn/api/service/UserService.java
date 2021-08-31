package com.orbvpn.api.service;

import com.orbvpn.api.config.security.JwtTokenUtil;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserProfileEdit;
import com.orbvpn.api.domain.dto.UserProfileView;
import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.PasswordReset;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserDeviceInfo;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.BadCredentialsException;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.UserCreateMapper;
import com.orbvpn.api.mapper.UserProfileEditMapper;
import com.orbvpn.api.mapper.UserProfileViewMapper;
import com.orbvpn.api.mapper.UserSubscriptionViewMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.PasswordResetRepository;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.UserProfileRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final UserCreateMapper userCreateMapper;
  private final UserViewMapper userViewMapper;

  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final JavaMailSender javaMailSender;

  private final PasswordResetRepository passwordResetRepository;
  private final PaymentRepository paymentRepository;

  private final UserProfileRepository userProfileRepository;
  private final UserProfileEditMapper userProfileEditMapper;
  private final UserProfileViewMapper userProfileViewMapper;
  private final UserSubscriptionViewMapper userSubscriptionViewMapper;

  private final RoleService roleService;
  private final ResellerService resellerService;
  private final GroupService groupService;
  private final UserSubscriptionService userSubscriptionService;
  private final RadiusService radiusService;
  private final PasswordService passwordService;
  private final PaymentService paymentService;

  @PostConstruct
  public void init() {
    paymentService.setUserService(this);
  }

  public AuthenticatedUser register(UserCreate userCreate) {
    log.info("Creating user with data {}", userCreate);

    Optional<User> userEntityOptional = userRepository.findByEmail(userCreate.getEmail());
    if (userEntityOptional.isPresent()) {
      throw new BadRequestException("User with specified email exists");
    }

    User user = userCreateMapper.createEntity(userCreate);
    user.setUsername(userCreate.getEmail());
    passwordService.setPassword(user, userCreate.getPassword());
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(resellerService.getOwnerReseller());

    userRepository.save(user);

    assignTrialSubscription(user);

    UserView userView = userViewMapper.toView(user);
    log.info("Created user {}", userView);

    return login(user);
  }

  public void assignTrialSubscription(User user) {
    // Assign trial group
    Group group = groupService.getById(1);
    String paymentId = UUID.randomUUID().toString();
    Payment payment = Payment.builder()
      .user(user)
      .status(PaymentStatus.PENDING)
      .gateway(GatewayName.FREE)
      .category(PaymentCategory.GROUP)
      .price(group.getPrice())
      .groupId(group.getId())
      .paymentId(paymentId)
      .build();
    paymentRepository.save(payment);

    paymentService.fullFillPayment(payment);
  }

  public String generateRandomString() {
    int length = 10;
    boolean useLetters = true;
    boolean useNumbers = true;
    return RandomStringUtils.random(length, useLetters, useNumbers);
  }

  public AuthenticatedUser login(String email, String password) {
    log.info("Authentication user with email {}", email);

    Authentication authentication;
    try {
      authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (Exception ex) {
      throw new BadCredentialsException(ex);
    }


    User user = (User) authentication.getPrincipal();
    return login(user);
  }

  public AuthenticatedUser login(User user) {
    UserView userView = userViewMapper.toView(user);
    String token = jwtTokenUtil.generateAccessToken(user);

    AuthenticatedUser authenticatedUser = new AuthenticatedUser(token, userView);
    return authenticatedUser;
  }

  public boolean requestResetPassword(String email) {
    log.info("Resetting password for user: {}", email);

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new NotFoundException("User with specified email not exists"));

    String token = generateRandomString();

    PasswordReset passwordReset = new PasswordReset();
    passwordReset.setUser(user);
    passwordReset.setToken(token);
    passwordResetRepository.save(passwordReset);

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(email);

    msg.setSubject("Reset your password");
    msg.setText(MessageFormat.format("Please use code: {0} to update yout password", token));

    javaMailSender.send(msg);

    passwordResetRepository.deleteByUserAndTokenNot(user, token);
    return true;
  }

  public boolean resetPassword(String token, String password) {

    PasswordReset passwordReset = passwordResetRepository.findById(token).orElseThrow(() ->
      new NotFoundException("Token was not found"));

    User user = passwordReset.getUser();
    passwordService.setPassword(user, password);
    userRepository.save(user);
    passwordResetRepository.delete(passwordReset);

    return true;
  }

  public boolean changePassword(int id, String oldPassword, String password) {
    log.info("Changing password for user with id {}", id);

    User user = userRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("User not found"));

    String oldPasswordEncoded = user.getPassword();
    if (!passwordEncoder.matches(oldPassword, oldPasswordEncoded)) {
      throw new BadRequestException("Wrong password");
    }

    passwordService.setPassword(user, password);
    userRepository.save(user);

    return true;
  }

  public void deleteUser(User user) {
    paymentService.deleteUserPayments(user);
    userSubscriptionService.deleteUserSubscriptions(user);
    radiusService.deleteUserRadChecks(user);
    radiusService.deleteUserRadAcct(user);
    userRepository.delete(user);
  }

  public User deleteOauthUser(String oauthId) {
    User user = userRepository.findByOauthId(oauthId)
      .orElseThrow(()->new NotFoundException("User not found"));

    deleteUser(user);

    return user;
  }

  public UserProfileView editProfile(UserProfileEdit userProfileEdit) {
    User user = getUser();

    log.info("Editing user{} profile{}", user.getId(), userProfileEdit);

    UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());

    UserProfile edited = userProfileEditMapper.edit(userProfile, userProfileEdit);
    edited.setUser(user);

    userProfileRepository.save(edited);

    return userProfileViewMapper.toView(edited);
  }

  public UserProfileView getProfile() {
    User user = getUser();

    UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());

    return userProfileViewMapper.toView(userProfile);
  }

  public UserSubscriptionView getUserSubscription() {
    User user = getUser();
    UserSubscription currentSubscription = userSubscriptionService.getCurrentSubscription(user);
    return userSubscriptionViewMapper.toView(currentSubscription);
  }

  public List<UserDeviceInfo> getUserDeviceInfo() {
    User user = getUser();
    String username = user.getUsername();

    List<String> allDevices = userRepository.findAllUserDevices(username);
    List<String> allActiveDevices = userRepository.findAllActiveUserDevices(username);

    return allDevices.stream()
      .filter(StringUtils::isNoneBlank)
      .map(s -> {
        UserDeviceInfo userDeviceInfo = new UserDeviceInfo();
        userDeviceInfo.setName(s);
        userDeviceInfo.setActive(allActiveDevices.contains(s));
        return userDeviceInfo;
      }).collect(Collectors.toList());
  }

  public User getUserById(int id) {
    return userRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(User.class, id));
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new NotFoundException(User.class, email));
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new NotFoundException(User.class, username));
  }

  public User getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }

  public Role getUserRole() {
    User user = getUser();
    return user.getRole();
  }

  public boolean isAdmin() {
    User user = getUser();
    return user.getRole().getName() == RoleName.ADMIN;
  }

  public UserView getUserView() {
    User user = getUser();

    return getUserFullView(user);
  }

  public UserView getUserFullView(User user) {
    UserSubscription subscription = userSubscriptionService.getCurrentSubscription(user);
    user.setSubscription(subscription);

    UserView userView = userViewMapper.toView(user);
    userView.setUserDevicesInfo(getUserDeviceInfo());
    return userView;
  }
}
