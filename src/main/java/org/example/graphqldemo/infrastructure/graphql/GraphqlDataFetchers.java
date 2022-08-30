package org.example.graphqldemo.infrastructure.graphql;

import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.dataloader.DataLoader;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.ProductPayload;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

/**
 * The type Graphql data fetchers.
 */
public class GraphqlDataFetchers {
  private static final String ID = "id";

  private final UserRepository repository;

  public GraphqlDataFetchers(UserRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets user.
   */
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

  /**
   * Gets user product.
   */
  public DataFetcher<CompletableFuture<Product>> getUserProduct() {
    return env -> {
      Context context = env.getLocalContext();
      String productId = env.getArgument(ID);
      return repository.getUserProduct(context.userId, productId);
    };
  }

  /**
   * List user products data fetcher.
   */
  public DataFetcher<CompletableFuture<Connection<Product>>> listUserProducts() {
    return env -> repository
        .listUserProducts(GraphqlTranslator.listUserProductsSpec(env))
        .thenApply(products -> new SimpleListConnection<>(
            products, "user-products:").get(env));
  }

  /**
   * Count user products data fetcher.
   */
  public DataFetcher<CompletableFuture<Long>> countUserProducts() {
    return env -> repository
        .countUserProducts(GraphqlTranslator.listUserProductsSpecForCount(env));
  }

  /**
   * List product users data fetcher.
   */
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

  /**
   * Create product data fetcher.
   */
  public DataFetcher<CompletableFuture<?>> createProduct() {
    return env -> guard(env, () -> repository
        .createProduct(GraphqlTranslator.createProductInput(env)));
  }

  /**
   * Update product data fetcher.
   */
  public DataFetcher<CompletableFuture<?>> updateProduct() {
    return env -> guard(env, () -> repository
        .updateProduct(GraphqlTranslator.updateProductInput(env)));
  }

  /**
   * Delete product data fetcher.
   */
  public DataFetcher<CompletableFuture<ProductPayload>> deleteProduct() {
    return env -> repository
        .deleteProduct(GraphqlTranslator.deleteProductInput(env));
  }

  /**
   * Add users to product data fetcher.
   */
  public DataFetcher<CompletableFuture<?>> addUsersToProduct() {
    return env -> guard(env, () -> repository
        .addUsersToProduct(GraphqlTranslator.addUsersToProductInput(env)));
  }

  /**
   * Remove users to product data fetcher.
   */
  public DataFetcher<CompletableFuture<?>> removeUsersFromProduct() {
    return env -> guard(env, () -> repository
        .removeUsersFromProduct(GraphqlTranslator
                                    .removeUsersFromProductInput(env)));
  }

  private static DataLoader<String, User> getUsersDataLoader(
      DataFetchingEnvironment env) {
    return env.getDataLoader(DataLoaderNames.USERS);
  }

  private static <T> CompletableFuture<?> guard(
      DataFetchingEnvironment env, Supplier<CompletableFuture<T>> supplier) {
    return supplier.get();
  }
}
