package org.example.graphqldemo.infrastructure.graphql;

import graphql.schema.idl.RuntimeWiring;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class GraphQLRuntimeWiring {
  public static RuntimeWiring buildWiring(GraphQLDataFetchers fetchers) {
    return RuntimeWiring.newRuntimeWiring()
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
