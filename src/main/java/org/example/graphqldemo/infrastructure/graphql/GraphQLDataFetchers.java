package org.example.graphqldemo.infrastructure.graphql;

import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.ProductPayload;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GraphQLDataFetchers {
  private static final String ID = "id";

  private final UserRepository repository;

  public GraphQLDataFetchers(UserRepository repository) {
    this.repository = repository;
  }

  public DataFetcher<CompletableFuture<User>> getUser() {
    return env -> {
      Context context = env.getLocalContext();
      return repository.getUser(context.userId);
    };
  }

  public DataFetcher<CompletableFuture<Product>> getUserProduct() {
    return env -> {
      Context context = env.getLocalContext();
      String productId = env.getArgument(ID);
      return repository.getUserProduct(context.userId, productId);
    };
  }

  public DataFetcher<CompletableFuture<Connection<Product>>> listUserProducts() {
    return env -> repository
        .listUserProducts(GraphQLTranslator.listUserProductsSpec(env))
        .thenApply(products -> new SimpleListConnection<>(
            products, "user-products:").get(env));
  }

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

  public DataFetcher<CompletableFuture<ProductPayload>> createProduct() {
    return env -> repository
        .createProduct(GraphQLTranslator.createProductInput(env));
  }

  public DataFetcher<CompletableFuture<ProductPayload>> updateProduct() {
    return env -> repository
        .updateProduct(GraphQLTranslator.updateProductInput(env));
  }

  public DataFetcher<CompletableFuture<ProductPayload>> deleteProduct() {
    return env -> repository
        .deleteProduct(GraphQLTranslator.deleteProductInput(env));
  }
}