package com.orbvpn.api.service;

import com.orbvpn.api.config.security.JwtTokenUtil;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserProfileEdit;
import com.orbvpn.api.domain.dto.UserProfileView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.PasswordReset;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.BadCredentialsException;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.UserCreateMapper;
import com.orbvpn.api.mapper.UserProfileEditMapper;
import com.orbvpn.api.mapper.UserProfileViewMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.PasswordResetRepository;
import com.orbvpn.api.reposiitory.UserProfileRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final UserProfileRepository userProfileRepository;
  private final UserProfileEditMapper userProfileEditMapper;
  private final UserProfileViewMapper userProfileViewMapper;

  private final RoleService roleService;
  private final ResellerService resellerService;
  private final GroupService groupService;
  private final UserSubscriptionService userSubscriptionService;


  public UserView register(UserCreate userCreate) {
    log.info("Creating user with data {}", userCreate);

    Optional<User> userEntityOptional = userRepository.findByEmail(userCreate.getEmail());
    if (userEntityOptional.isPresent()) {
      throw new BadRequestException("User with specified email exists");
    }

    User user = userCreateMapper.createEntity(userCreate);
    user.setUsername(userCreate.getEmail());
    user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
    user.setRadAccess(UUID.randomUUID().toString());
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(resellerService.getOwnerReseller());

    userRepository.save(user);
    UserView userView = userViewMapper.toView(user);

    // Assign trial group
    Group group = groupService.getById(1);
    String paymentId = UUID.randomUUID().toString();
    UserSubscription userSubscription = userSubscriptionService
      .createUserSubscription(user, group, PaymentType.RESELLER_CREDIT, PaymentStatus.PENDING,
        paymentId);
    userSubscriptionService.fullFillSubscription(userSubscription);

    log.info("Created user {}", userView);

    return userView;
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

    String token = UUID.randomUUID().toString();

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
    user.setPassword(passwordEncoder.encode(password));
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

    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    return true;
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

  public User getUserById(int id) {
    return userRepository.findById(id)
      .orElseThrow(()->new NotFoundException(User.class, id));
  }

  public User getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }

  public Role getUserRole() {
    User user = getUser();
    return user.getRole();
  }

  public UserView getUserView() {
    User user = getUser();

    return userViewMapper.toView(user);
  }
}
