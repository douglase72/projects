package com.erdouglass.common.graphql;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.parser.Parser;
import io.smallrye.graphql.api.Context;
import io.smallrye.graphql.api.ErrorCode;
import io.smallrye.graphql.cdi.event.AfterExecute;
import io.smallrye.graphql.cdi.event.BeforeExecute;

@ApplicationScoped
public class GraphQLLogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(GraphQLLogInterceptor.class);

  public void onRequest(@Observes @BeforeExecute Context context) {
    LOGGER.infof("Request: %s", queryName(context.getQuery()));
  }
  
  public void onResponse(@Observes @AfterExecute Context context) {
    var result = context.unwrap(ExecutionResult.class);
    var errors = result.getErrors();
    if (errors.isEmpty()) {
      LOGGER.info("Response: 200 (OK)");
      return;
    }
    var code = errors.stream().map(this::code).findFirst().orElse("unknown");
    LOGGER.errorf("Response: %s errors %s", code, errors);
  }
  
  private String code(GraphQLError error) {
    var ext = error.getExtensions();
    if (ext != null && ext.get("code") != null) {
      return String.valueOf(ext.get("code"));
    }
    if (error instanceof ExceptionWhileDataFetching ewdf) {
      var ann = ewdf.getException().getClass().getAnnotation(ErrorCode.class);
      if (ann != null) {
        return ann.value();
      }
    }
    return String.valueOf(error.getErrorType());
  }
  
  private static String queryName(String query) {
    try {
      return Parser.parse(query).getDefinitionsOfType(OperationDefinition.class).stream()
          .flatMap(op -> op.getSelectionSet().getSelections().stream())
          .filter(Field.class::isInstance)
          .map(field -> ((Field) field).getName())
          .collect(Collectors.joining(", "));
    } catch (Exception e) {
      return query;
    }
  }  
}
