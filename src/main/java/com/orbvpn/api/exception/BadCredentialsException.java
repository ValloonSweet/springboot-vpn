package com.orbvpn.api.exception;

public class BadCredentialsException extends RuntimeException {

  public BadCredentialsException(Exception exception) {
    super("Wrong credentials, " + exception.getMessage());
  }
}
