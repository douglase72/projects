package com.erdouglass.common.graphql;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.language.Field;
import graphql.language.ObjectValue;
import graphql.language.OperationDefinition;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.parser.Parser;
import io.smallrye.graphql.api.Context;
import io.smallrye.graphql.api.ErrorCode;
import io.smallrye.graphql.cdi.event.AfterExecute;
import io.smallrye.graphql.cdi.event.BeforeExecute;

@ApplicationScoped
public class GraphQLLogObserver {
  private static final Logger LOGGER = Logger.getLogger(GraphQLLogObserver.class);

  public void onRequest(@Observes @BeforeExecute Context context) {
    var variables = context.getVariables().orElseGet(Map::of);
    LOGGER.infof("Request: POST /graphql/%s", path(context.getQuery(), variables));
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
  
  private static String path(String query, Map<String, Object> variables) {
    try {
      return Parser.parse(query).getDefinitionsOfType(OperationDefinition.class).stream()
          .flatMap(op -> op.getSelectionSet().getSelections().stream())
          .filter(Field.class::isInstance)
          .map(Field.class::cast)
          .map(field -> field.getName() + findId(field, variables).map(id -> "/" + id).orElse(""))
          .collect(Collectors.joining(", "));
    } catch (Exception e) {
      return query;
    }
  }

  private static Optional<String> findId(Field field, Map<String, Object> variables) {
    return field.getArguments().stream()
        .flatMap(arg -> fromAst(arg.getName(), arg.getValue(), variables))
        .findFirst();
  }

  private static Stream<String> fromAst(String name, Value<?> value, Map<String, Object> variables) {
    if (value instanceof VariableReference ref) {
      return fromVariable(name, variables.get(ref.getName()));
    }
    if (value instanceof ObjectValue object) {
      return object.getObjectFields().stream()
          .flatMap(f -> fromAst(f.getName(), f.getValue(), variables));
    }
    if (!"id".equals(name)) {
      return Stream.empty();
    }
    return Stream.of(value instanceof StringValue s ? s.getValue() : String.valueOf(value));
  }

  private static Stream<String> fromVariable(String name, Object value) {
    if (value instanceof Map<?, ?> map) {
      return map.entrySet().stream()
          .flatMap(e -> fromVariable(String.valueOf(e.getKey()), e.getValue()));
    }
    if ("id".equals(name) && value != null) {
      return Stream.of(String.valueOf(value));
    }
    return Stream.empty();
  }
}
