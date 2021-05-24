package com.orbvpn.api.service;


import com.orbvpn.api.domain.entity.Nas;
import com.orbvpn.api.domain.entity.RadCheck;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.NasRepository;
import com.orbvpn.api.reposiitory.RadCheckRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadiusService {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd yyyy HH:mm:ss");

  private final NasRepository nasRepository;
  private final RadCheckRepository radCheckRepository;

  public void createNas(Server server) {
    Nas nas = new Nas();
    mapNas(nas, server);
    nasRepository.save(nas);
  }

  public void editNas(String nasname, Server server) {
    Nas nas = getNasByName(nasname);
    mapNas(nas, server);
    nasRepository.save(nas);
  }

  public void deleteNas(Server server) {
    Nas nas = getNasByName(server.getPublicIp());
    nasRepository.delete(nas);
  }

  public void mapNas(Nas nas, Server server) {
    nas.setNasName(server.getPublicIp());
    nas.setShortName(server.getHostName());
    nas.setType(server.getType());
    nas.setSecret(server.getSecret());
  }

  @Transactional
  public void createUserRadChecks(UserSubscription userSubscription) {
    User user = userSubscription.getUser();

    //Password
    String sha1Hex = DigestUtils.sha1Hex(user.getRadAccess());
    RadCheck passwordCheck = new RadCheck();
    passwordCheck.setUsername(user.getUsername());
    passwordCheck.setAttribute("SHA-Password");
    passwordCheck.setOp(":=");
    passwordCheck.setValue(sha1Hex);

    //Simultaneous use
    RadCheck simultaneousCheck = new RadCheck();
    simultaneousCheck.setUsername(user.getEmail());
    simultaneousCheck.setAttribute("Simultaneous-Use");
    simultaneousCheck.setOp(":=");
    simultaneousCheck.setValue(String.valueOf(userSubscription.getMultiLoginCount()));

    //Expiration
    RadCheck expirationCheck = new RadCheck();
    expirationCheck.setUsername(user.getEmail());
    expirationCheck.setAttribute("Expiration");
    expirationCheck.setOp("==");
    expirationCheck.setValue(convertToExpirationString(userSubscription.getExpiresAt()));

    radCheckRepository.save(passwordCheck);
    radCheckRepository.save(simultaneousCheck);
    radCheckRepository.save(expirationCheck);
  }

  public String convertToExpirationString(LocalDateTime expiration) {
    ZonedDateTime ldtZoned = expiration.atZone(ZoneId.systemDefault());
    ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
    return DATE_FORMATTER.format(utcZoned);
  }

  public Nas getNasByName(String nasName) {
    return nasRepository.findByNasName(nasName)
      .orElseThrow(()->new NotFoundException(Nas.class, nasName));
  }
}
