package com.orbvpn.api.exception;

public class BadCredentialsException extends RuntimeException{
  public BadCredentialsException() {
    super("Wrong credentials");
  }
}
