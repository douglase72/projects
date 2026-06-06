package com.erdouglass.emdb.media.logging;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import graphql.ExecutionResult;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.parser.Parser;
import io.smallrye.graphql.api.Context;
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
    if (result.getErrors().isEmpty()) {
      LOGGER.info("Response: 200 (OK)");
    } else {
      LOGGER.errorf("Response: errors %s", result.getErrors());
    }
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
