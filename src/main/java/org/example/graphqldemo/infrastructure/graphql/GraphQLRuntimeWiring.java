package org.example.graphqldemo.infrastructure.graphql;

import graphql.schema.idl.RuntimeWiring;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class GraphQLRuntimeWiring {
  private final GraphQLDataFetchers fetchers;

  public GraphQLRuntimeWiring(GraphQLDataFetchers fetchers) {
    this.fetchers = fetchers;
  }

  public void addTypeWiring(RuntimeWiring.Builder wiring) {
    wiring
        .type(newTypeWiring("Query")
                  .dataFetcher("viewer", fetchers.getUser()))
        .type(newTypeWiring("User")
                  .dataFetcher("products", fetchers.listUserProducts()))
        .type(newTypeWiring("UserProductConnection")
                  .dataFetcher("totalCount", fetchers.countUserProducts()))
        .type(newTypeWiring("Product")
                  .dataFetcher("users", fetchers.listProductUsers()))
        .build();
  }
}
