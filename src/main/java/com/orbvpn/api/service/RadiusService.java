package com.orbvpn.api.service;


import com.orbvpn.api.domain.entity.Nas;
import com.orbvpn.api.domain.entity.RadCheck;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.NasRepository;
import com.orbvpn.api.reposiitory.RadCheckRepository;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadiusService {

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
    nas.setShortName("shorname");
    nas.setType("other");
    nas.setSecret(server.getSecret());
  }

  public void createUserRadChecks(User user) {

    //Password
    String sha1Hex = DigestUtils.sha1Hex(user.getRadAccess());
    RadCheck passwordCheck = new RadCheck();
    passwordCheck.setUsername(user.getEmail());
    passwordCheck.setAttribute("SHA-Password");
    passwordCheck.setOp(":=");
    passwordCheck.setValue(sha1Hex);

    //Simultaneous use
    RadCheck simultaneousCheck = new RadCheck();
    passwordCheck.setUsername(user.getEmail());
    passwordCheck.setAttribute("Simultaneous-Use");
    passwordCheck.setOp(":=");
    passwordCheck.setValue("10");

    //Expiration
    RadCheck expirationCheck = new RadCheck();
    passwordCheck.setUsername(user.getEmail());
    passwordCheck.setAttribute("Expiration");
    passwordCheck.setOp("==");
    passwordCheck.setValue("November 30 2023 00:00:00");

    radCheckRepository.save(passwordCheck);
    radCheckRepository.save(simultaneousCheck);
    radCheckRepository.save(expirationCheck);
  }

  public Nas getNasByName(String nasName) {
    return nasRepository.findByNasName(nasName)
      .orElseThrow(()->new NotFoundException(Nas.class, nasName));
  }
}
