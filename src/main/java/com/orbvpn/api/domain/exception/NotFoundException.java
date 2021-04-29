package com.orbvpn.api.domain.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;

public class NotFoundException extends RuntimeException implements GraphQLError {

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Class<?> clazz, long id) {
    super(String.format("Entity %s with compositeid %d not found", clazz.getSimpleName(), id));
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
