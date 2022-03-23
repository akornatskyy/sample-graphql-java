package org.example.graphqldemo.infrastructure.graphql;

import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.validation.rules.ValidationRules;
import org.dataloader.DataLoader;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.ProductPayload;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class GraphQLDataFetchers {
  private static final String ID = "id";

  private final UserRepository repository;

  public GraphQLDataFetchers(UserRepository repository) {
    this.repository = repository;
  }

  public DataFetcher<CompletableFuture<?>> getUser() {
    return env -> {
      Context context = env.getLocalContext();
      // return repository.getUser(context.userId);
      return getUsersDataLoader(env).load(context.userId)
          .thenApply(user -> {
            if (user == null) {
              throw new IllegalStateException("User is not found.");
            }

            return user;
          });
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
      Context context = env.getLocalContext();
      Product product = env.getSource();
      ListProductUsersSpec spec = new ListProductUsersSpec();
      spec.userId = context.userId;
      spec.productId = product.id;
      // return repository.listProductUsers(spec);
      return repository.listProductUserIds(spec)
          .thenCompose(ids -> getUsersDataLoader(env).loadMany(ids));
    };
  }

  public DataFetcher<CompletableFuture<?>> createProduct() {
    return env -> guard(env, () -> repository
        .createProduct(GraphQLTranslator.createProductInput(env)));
  }

  public DataFetcher<CompletableFuture<?>> updateProduct() {
    return env -> guard(env, () -> repository
        .updateProduct(GraphQLTranslator.updateProductInput(env)));
  }

  public DataFetcher<CompletableFuture<ProductPayload>> deleteProduct() {
    return env -> repository
        .deleteProduct(GraphQLTranslator.deleteProductInput(env));
  }

  private static DataLoader<String, User> getUsersDataLoader(
      DataFetchingEnvironment env) {
    return env.getDataLoader(DataLoaderNames.USERS);
  }

  private static <T> CompletableFuture<?> guard(
      DataFetchingEnvironment env, Supplier<CompletableFuture<T>> supplier) {
    List<GraphQLError> errors = ValidationRules.newValidationRules()
        .build()
        .runValidationRules(env);
    if (!errors.isEmpty()) {
      return CompletableFuture.completedFuture(
          DataFetcherResult.newResult()
              .errors(errors)
              .build());
    }

    return supplier.get();
  }
}
