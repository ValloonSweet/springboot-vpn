package com.orbvpn.api.exception.handler;

import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.NotFoundException;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Component
public class GraphQLExceptionHandler {

  @ExceptionHandler({BadRequestException.class})
  public ThrowableGraphQLError onBadRequestException(BadRequestException e) {
    return new ThrowableGraphQLError(e);
  }

  @ExceptionHandler({NotFoundException.class})
  public ThrowableGraphQLError onBadRequestException(NotFoundException e) {
    return new ThrowableGraphQLError(e);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ThrowableGraphQLError nConstraintViolation(ConstraintViolationException e) {
    return new ThrowableGraphQLError(e);
  }

  @ExceptionHandler({RuntimeException.class})
  public ThrowableGraphQLError onRuntimeException(RuntimeException e) {
    return new ThrowableGraphQLError(e);
  }

}
