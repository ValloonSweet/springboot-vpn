package com.orbvpn.api.domain.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;

public class BadRequestException extends RuntimeException implements GraphQLError {

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(Class<?> clazz, String message) {
    super(String
      .format("Wrong value submitted for class  %s with message %s", clazz.getSimpleName(),
        message));
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorClassification getErrorType() {
    return null;
  }
}
