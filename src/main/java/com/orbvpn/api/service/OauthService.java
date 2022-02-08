package com.orbvpn.api.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.orbvpn.api.domain.dto.*;
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
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.orbvpn.api.domain.OAuthConstants.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class OauthService {

  private static final HttpTransport transport = new NetHttpTransport();
  private static final JsonFactory jsonFactory = new GsonFactory();

  private final RoleService roleService;
  private final UserService userService;
  private final PasswordService passwordService;
  private final ResellerService resellerService;
  private final UserRepository userRepository;
  private final TokenService tokenService;

  public AuthenticatedUser oauthLogin(String token, SocialMedia socialMedia) {

    TokenData tokenData = getTokenData(token, socialMedia);

    User user = userRepository.findByUsername(tokenData.getEmail())
      .orElseGet(() -> createUser(tokenData));

    return userService.login(user);
  }

  public TokenData getToken(String code, SocialMedia socialMedia){

    String token = "";

    switch (socialMedia) {
      case GOOGLE:
        token = tokenService.getGoogleToken(code);
        break;
      case FACEBOOK:
        token = tokenService.getFacebookToken(code);
        break;
      case APPLE:
        token = tokenService.getAppleToken(code);
        break;
      case TWITTER:
        token = tokenService.getTwitterToken(code);
        break;
      case LINKEDIN:
        token = tokenService.getLinkedinToken(code);
        break;
      case AMAZON:
        token = tokenService.getAmazonToken(code);
        break;
      default:
        throw new OauthLoginException("Could not retrieve token from provider.");
    }

    return getTokenData(token, socialMedia);
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
      .setAudience(Collections.singletonList(googleClientId))
      .build();

    GoogleIdToken idTokenData;
    try {
      idTokenData = verifier.verify(token);
    } catch (Exception ex) {
      log.error(String.format("Exception occured while verifying Google user token : %s",ex.getCause() ));
      throw new OauthLoginException();
    }

    if (idTokenData == null) {
      log.error("idTokenData is null");
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

    TwitterConnectionFactory connectionFactory =
            new TwitterConnectionFactory(twitterClientId, twitterClientSecret);
    OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
    OAuthToken requestToken = null;
    try{
      requestToken = oauthOperations.fetchRequestToken("https://api.twitter.com/oauth/request_token", null);

    } catch (Exception ex){
      log.error(String.format("Twitter request token error : %s", ex.getMessage()));
      throw new OauthLoginException(ex.getMessage());
    }

    OAuthToken accessToken = oauthOperations.exchangeForAccessToken(
            new AuthorizedRequestToken(requestToken, ""), null);
    System.out.println("Token Value:- accesstoken");
    accessToken.getSecret();
    accessToken.getValue();
    Twitter twitter = new TwitterTemplate(twitterClientId,
            twitterClientSecret,
            accessToken.getValue(),
            accessToken.getSecret());
    TwitterProfile profile = twitter.userOperations().getUserProfile();
    System.out.println(profile.toString());

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

  // Connection Factory
  public TwitterConnectionFactory twitterConnectionFactory(){
    return new TwitterConnectionFactory( twitterClientId,twitterClientSecret );
  }

  //Since Twitter Supports Oauth 1.0 we used "oauth1Operations"
  public OAuth1Operations oAuth1Operations(TwitterConnectionFactory connectionFactory){
    return connectionFactory.getOAuthOperations();
  }

  //Create Twitter Template for fetching user email etc.
  public TwitterTemplate twitterTemplate( OAuthToken accessToken){
    return  new TwitterTemplate( twitterClientId, twitterClientSecret, accessToken.getValue(), accessToken.getSecret() );
  }

  public String twitterOauthLogin(){
    TwitterConnectionFactory connectionFactory = twitterConnectionFactory();
    OAuth1Operations oauthOperations = oAuth1Operations(connectionFactory);
    OAuthToken requestToken = oauthOperations.fetchRequestToken( twitterCallbackUrl, null );
    log.info(requestToken.getSecret()+" -----  "+ requestToken.getValue());

    return oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
  }


  public TwitterUserInfo twitterUserProfile(HttpServletRequest request, HttpServletResponse response){

    TwitterConnectionFactory connectionFactory = twitterConnectionFactory();
    OAuth1Operations oauthOperations =oAuth1Operations(connectionFactory);
    OAuthToken oAuthToken=new OAuthToken(request.getParameter("oauth_token"),request.getParameter("oauth_verifier"));

    OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(oAuthToken,request.getParameter("oauth_verifier")), null);

    TwitterTemplate twitterTemplate =twitterTemplate(accessToken);


    RestTemplate restTemplate = twitterTemplate.getRestTemplate();
    ObjectNode objectNode = restTemplate.getForObject(twitterUserInfoUrl, ObjectNode.class);

    String email = objectNode.get("email").asText();

    return new TwitterUserInfo(objectNode.get("name").asText(),
            objectNode.get("email").asText(),
            objectNode.get("description").asText(),
            objectNode.get("location").asText());
  }


}
