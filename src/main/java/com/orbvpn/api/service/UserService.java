package com.orbvpn.api.service;

import com.orbvpn.api.config.security.JwtTokenUtil;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.PasswordReset;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.exception.BadCredentialsException;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.UserCreateMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.PasswordResetRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final UserCreateMapper userCreateMapper;
  private final UserViewMapper userViewMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final JavaMailSender javaMailSender;
  private final PasswordResetRepository passwordResetRepository;


  public UserView register(UserCreate userCreate) {
    log.info("Creating user with data {}", userCreate);

    Optional<User> userEntityOptional = userRepository.findByEmail(userCreate.getEmail());
    if (userEntityOptional.isPresent()) {
      throw new BadRequestException("User with specified username exists");
    }

    User user = userCreateMapper.createEntity(userCreate);
    user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
    userRepository.save(user);

    UserView userView = userViewMapper.toView(user);

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
      throw new BadCredentialsException();
    }


    User user = (User) authentication.getPrincipal();

    UserView userView = userViewMapper.toView(user);
    String token = jwtTokenUtil.generateAccessToken(user);

    AuthenticatedUser authenticatedUser = new AuthenticatedUser(token, userView);
    return authenticatedUser;
  }

  @Transactional
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

  public UserView getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();

    return userViewMapper.toView(user);
  }
}
