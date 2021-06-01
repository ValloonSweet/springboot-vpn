package com.orbvpn.api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.TokenData;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.exception.OauthLoginException;
import com.orbvpn.api.reposiitory.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OauthService {

  private static final HttpTransport transport = new NetHttpTransport();
  private static final JsonFactory jsonFactory = new GsonFactory();

  private final RoleService roleService;
  private final UserService userService;
  private final ResellerService resellerService;
  private final UserRepository userRepository;

  @Value("${oauth.google.client-ids}")
  private List<String> googleClientIds;

  public AuthenticatedUser oauthLogin(String idToken, SocialMedia socialMedia) {

    TokenData tokenData = getTokenData(idToken, socialMedia);

    User user = userRepository.findByUsername(tokenData.getEmail())
      .orElseGet(() -> createUser(tokenData));


    return userService.login(user);
  }

  public TokenData getTokenData(String idToken, SocialMedia socialMedia) {

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      .setAudience(googleClientIds)
      .build();

    GoogleIdToken idTokenData;
    try {
      idTokenData = verifier.verify(idToken);
    } catch (Exception ex) {
      throw new OauthLoginException();
    }

    if (idTokenData == null) {
      throw new OauthLoginException();
    }

    Payload payload = idTokenData.getPayload();

    String email = payload.getEmail();
    String familyName = (String) payload.get("family_name");
    String givenName = (String) payload.get("given_name");
    long exp = payload.getExpirationTimeSeconds();
    long iat = payload.getIssuedAtTimeSeconds();

    return TokenData.builder()
      .email(email)
      .firstName(familyName)
      .lastName(givenName)
      .exp(exp)
      .iat(iat).build();
  }

  private User createUser(TokenData tokenData) {
    User user = new User();
    user.setUsername(tokenData.getEmail());
    user.setEmail(tokenData.getEmail());
    user.setFirstName(tokenData.getFirstName());
    user.setLastName(tokenData.getLastName());
    user.setRadAccess(UUID.randomUUID().toString());
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(resellerService.getOwnerReseller());

    userRepository.save(user);
    return user;
  }

}
