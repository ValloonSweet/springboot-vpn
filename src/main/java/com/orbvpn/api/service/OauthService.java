package com.orbvpn.api.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.FBTokenData;
import com.orbvpn.api.domain.dto.FBTokenMetadata;
import com.orbvpn.api.domain.dto.FBTokenMetadataWrapper;
import com.orbvpn.api.domain.dto.TokenData;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.exception.OauthLoginException;
import com.orbvpn.api.reposiitory.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.interfaces.RSAPublicKey;
import java.text.MessageFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class OauthService {

  private static final HttpTransport transport = new NetHttpTransport();
  private static final JsonFactory jsonFactory = new GsonFactory();

  private final RoleService roleService;
  private final UserService userService;
  private final PasswordService passwordService;
  private final ResellerService resellerService;
  private final UserRepository userRepository;

  @Value("${oauth.google.client-ids}")
  private List<String> googleClientIds;

  @Value("${oauth.facebook.app-id}")
  private String facebookAppId;

  @Value("${oauth.facebook.app-secret}")
  private String facebookAppSecret;

  public AuthenticatedUser oauthLogin(String token, SocialMedia socialMedia) {

    TokenData tokenData = getTokenData(token, socialMedia);

    User user = userRepository.findByUsername(tokenData.getEmail())
      .orElseGet(() -> createUser(tokenData));


    return userService.login(user);
  }

  public TokenData getTokenData(String token, SocialMedia socialMedia) {
    switch (socialMedia) {
      case GOOGLE:
        return getGoogleTokenData(token);
      case FACEBOOK:
        return getFacebookTokenData(token);
      case APPLE:
        return getAppleTokenData(token);
      case TWITTER:
        return getTwitterTokenData(token);
      case LINKEDIN:
        return getLinkedinTokenData(token);
      case AMAZON:
        return getAmazonTokenData(token);
      default:
        throw new OauthLoginException();
    }
  }

  private User createUser(TokenData tokenData) {
    String password = userService.generateRandomString();

    User user = new User();
    user.setUsername(tokenData.getEmail());
    user.setEmail(tokenData.getEmail());
    passwordService.setPassword(user, password);
    user.setRadAccessClear(password);
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(resellerService.getOwnerReseller());
    user.setOauthId(tokenData.getOauthId());

    userRepository.save(user);

    userService.assignTrialSubscription(user);

    return user;
  }

  private TokenData getGoogleTokenData(String token) {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      .setAudience(googleClientIds)
      .build();

    GoogleIdToken idTokenData;
    try {
      idTokenData = verifier.verify(token);
    } catch (Exception ex) {
      throw new OauthLoginException();
    }

    if (idTokenData == null) {
      throw new OauthLoginException();
    }

    Payload payload = idTokenData.getPayload();

    String email = payload.getEmail();
    long exp = payload.getExpirationTimeSeconds();
    long iat = payload.getIssuedAtTimeSeconds();

    return TokenData.builder()
      .email(email)
      .exp(exp)
      .iat(iat).build();
  }

  private TokenData getFacebookTokenData(String token) {
    RestTemplate debugRequest = new RestTemplate();
    String debugUrl = MessageFormat.format("https://graph.facebook.com/debug_token?input_token={0}&access_token={1}|{2}",token, facebookAppId, facebookAppSecret);
    ResponseEntity<FBTokenMetadataWrapper> fbTokenMetadataWrapperResponse = debugRequest.getForEntity(debugUrl, FBTokenMetadataWrapper.class);

    FBTokenMetadata fbTokenMetadata = fbTokenMetadataWrapperResponse.getBody().getData();

    if(fbTokenMetadata == null) {
      throw new OauthLoginException();
    }

    String appId = fbTokenMetadata.getAppId();

    if(appId == null || !appId.equals(facebookAppId)) {
      throw new OauthLoginException();
    }

    RestTemplate dataRequest = new RestTemplate();
    String dataUrl = MessageFormat.format("https://graph.facebook.com/me?fields=email&access_token={0}", token);
    ResponseEntity<FBTokenData> fbTokenDataResponse = dataRequest.getForEntity(dataUrl, FBTokenData.class);
    FBTokenData fbTokenData = fbTokenDataResponse.getBody();

    return TokenData.builder()
      .email(fbTokenData.getEmail())
      .oauthId(fbTokenData.getId())
      .build();

  }

  private TokenData getAppleTokenData(String encryptedToken) {
    try {
      DecodedJWT jwt = JWT.decode(encryptedToken);

      JwkProvider provider = new UrlJwkProvider("https://appleid.apple.com/auth/keys");
      Jwk jwk = provider.get(jwt.getKeyId());
      Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
      algorithm.verify(jwt);

      Claims claims = Jwts.parser().setSigningKey((RSAPublicKey)
        jwk.getPublicKey()).parseClaimsJws(encryptedToken).getBody();

      return TokenData.builder()
        .email(claims.get("email", String.class))
        .build();
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

  private TokenData getTwitterTokenData(String encryptedToken) {
    try {
      return null;
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

  private TokenData getLinkedinTokenData(String encryptedToken) {
    try {
      return null;
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

  private TokenData getAmazonTokenData(String encryptedToken) {
    try {
      return null;
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

}
