package com.orbvpn.api.exception;

public class OauthLoginException extends RuntimeException {

  public OauthLoginException() {
    super("Oauth login failed");
  }
}
