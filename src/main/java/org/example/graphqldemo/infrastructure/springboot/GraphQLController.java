package org.example.graphqldemo.infrastructure.springboot;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.graphqldemo.core.Context;
import org.springframework.http.MediaType;
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

  public GraphQLController(GraphQL graphql) {
    this.graphql = graphql;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/graphql",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @CrossOrigin
  public CompletableFuture<Map<String, Object>> graphql(
      @RequestBody Map<String, Object> body,
      @RequestHeader("x-user") String user) {
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
                .build())
        .thenApply(ExecutionResult::toSpecification);
  }
}