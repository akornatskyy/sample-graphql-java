package org.example.graphqldemo.infrastructure.springboot;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.infrastructure.graphql.DataLoaderRegistryFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class GraphQLController {
  private final GraphQL graphql;
  private final DataLoaderRegistryFactory registryFactory;

  public GraphQLController(
      GraphQL graphql, DataLoaderRegistryFactory registryFactory) {
    this.graphql = graphql;
    this.registryFactory = registryFactory;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/graphql",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @CrossOrigin
  public CompletableFuture<ResponseEntity<?>> graphql(
      @RequestBody Map<String, Object> body,
      @RequestHeader(value = "x-user", required = false) String user) {
    if (user == null) {
      return CompletableFuture.completedFuture(
          ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .build());
    }

    String query = (String) body.getOrDefault("query", "");
    String operationName = (String) body.get("operationName");
    Map<String, Object> variables = (Map<String, Object>) body.get("variables");
    if (variables == null) {
      variables = new LinkedHashMap<>();
    }

    Context context = new Context();
    context.userId = user;

    return this.graphql.executeAsync(
            ExecutionInput.newExecutionInput()
                .query(query)
                .operationName(operationName)
                .variables(variables)
                .localContext(context)
                .dataLoaderRegistry(registryFactory.create())
                .build())
        .thenApply(ExecutionResult::toSpecification)
        .thenApply(ResponseEntity::ok);
  }
}