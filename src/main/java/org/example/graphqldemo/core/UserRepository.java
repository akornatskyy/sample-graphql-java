package org.example.graphqldemo.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The interface User repository.
 */
public interface UserRepository {
  CompletableFuture<List<User>> getUsers(List<String> ids);

  CompletableFuture<Product> getUserProduct(String userId, String productId);

  CompletableFuture<List<Product>> listUserProducts(ListUserProductsSpec spec);

  CompletableFuture<Long> countUserProducts(ListUserProductsSpec spec);

  CompletableFuture<List<String>> listProductUserIds(ListProductUsersSpec spec);

  CompletableFuture<ProductPayload> createProduct(
      CreateProductInput input);

  CompletableFuture<ProductPayload> updateProduct(
      UpdateProductInput input);

  CompletableFuture<ProductPayload> deleteProduct(
      DeleteProductInput input);

  CompletableFuture<ProductPayload> addUsersToProduct(
      AddUsersToProductInput input);

  CompletableFuture<ProductPayload> removeUsersFromProduct(
      RemoveUsersFromProductInput input);
}
