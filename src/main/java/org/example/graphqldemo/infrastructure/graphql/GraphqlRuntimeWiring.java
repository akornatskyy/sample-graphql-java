package org.example.graphqldemo.infrastructure.graphql;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

/**
 * The type Graphql runtime wiring.
 */
public class GraphqlRuntimeWiring {
  private final GraphqlDataFetchers fetchers;

  public GraphqlRuntimeWiring(GraphqlDataFetchers fetchers) {
    this.fetchers = fetchers;
  }

  /**
   * Add type wiring.
   */
  public void addTypeWiring(RuntimeWiring.Builder wiring) {
    wiring
        .type(TypeRuntimeWiring.newTypeWiring("Query")
                  .dataFetcher("viewer", fetchers.getUser()))
        .type(TypeRuntimeWiring.newTypeWiring("User")
                  .dataFetcher("product", fetchers.getUserProduct())
                  .dataFetcher("products", fetchers.listUserProducts()))
        .type(TypeRuntimeWiring.newTypeWiring("UserProductConnection")
                  .dataFetcher("totalCount", fetchers.countUserProducts()))
        .type(TypeRuntimeWiring.newTypeWiring("Product")
                  .dataFetcher("users", fetchers.listProductUsers()))
        .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                  .dataFetcher("createProduct", fetchers.createProduct())
                  .dataFetcher("updateProduct", fetchers.updateProduct())
                  .dataFetcher("deleteProduct", fetchers.deleteProduct())
                  .dataFetcher("addUsersToProduct", fetchers.addUsersToProduct()))
        .build();
  }
}
