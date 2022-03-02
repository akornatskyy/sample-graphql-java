package org.example.graphqldemo.infrastructure.graphql;

import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GraphQLDataFetchers {
  private final UserRepository repository;

  public GraphQLDataFetchers(UserRepository repository) {
    this.repository = repository;
  }

  public DataFetcher<CompletableFuture<User>> getUser() {
    return env -> {
      Context context = env.getLocalContext();
      return repository.findUserById(context.userId);
    };
  }

  public DataFetcher<CompletableFuture<Connection<Product>>> listUserProducts() {
    return env -> repository
        .listUserProducts(GraphQLTranslator.listUserProductsSpec(env))
        .thenApply(products -> new SimpleListConnection<>(
            products, "user-products:").get(env));
  }

  /*
  public DataFetcher<CompletableFuture<List<Product>>> listUserProducts() {
    return env -> {
      Context context = env.getLocalContext();
      ListUserProductsSpec spec = new ListUserProductsSpec();
      spec.userId = context.userId;
      spec.type = env.getArgument("type");
      return repository.listUserProducts(spec);
    };
  }*/

  public DataFetcher<CompletableFuture<Long>> countUserProducts() {
    return env -> repository
        .countUserProducts(GraphQLTranslator.listUserProductsSpecForCount(env));
  }

  public DataFetcher<CompletableFuture<List<User>>> listProductUsers() {
    return env -> {
      Product product = env.getSource();
      ListProductUsersSpec spec = new ListProductUsersSpec();
      spec.productId = product.id;
      return repository.listProductUsers(spec);
    };
  }
}