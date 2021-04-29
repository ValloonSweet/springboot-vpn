package com.orbvpn.api.service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.orbvpn.api.config.security.JwtTokenUtil;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.LoginCredentials;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.exception.BadRequestException;
import com.orbvpn.api.domain.exception.NotFoundException;
import com.orbvpn.api.mapper.UserCreateMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
  private final JWTVerifier jwtVerifier;
  private final JavaMailSender javaMailSender;

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

  public AuthenticatedUser login(LoginCredentials loginCredentials) {
    log.info("Authentication user with email {}", loginCredentials);

    Authentication authenticate = authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(),
        loginCredentials.getPassword()));

    User user = (User) authenticate.getPrincipal();

    UserView userView = userViewMapper.toView(user);
    String token = jwtTokenUtil.generateAccessToken(user);

    AuthenticatedUser authenticatedUser = new AuthenticatedUser(token);
    return authenticatedUser;
  }

  public boolean forgotPassword(String email) {
    log.info("Resetting password for user: {}", email);

    Optional<User> userEntityOptional = userRepository.findByEmail(email);
    if (userEntityOptional.isEmpty()) {
      throw new NotFoundException("User with specified email not exists");
    }

    String resetPasswordToken = jwtTokenUtil.generateResetPasswordToken(email);

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(email);

    msg.setSubject("Token");
    msg.setText(resetPasswordToken);

    javaMailSender.send(msg);

    return true;
  }

  public boolean resetPassword(String token, String password) {
    DecodedJWT decodedJWT = jwtVerifier.verify(token);

    String email = decodedJWT.getClaim("email").asString();

    User user = userRepository.findByEmail(email).orElseThrow();
    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    return true;
  }
}
