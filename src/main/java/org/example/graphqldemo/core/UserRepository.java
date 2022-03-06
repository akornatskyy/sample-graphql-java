package org.example.graphqldemo.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
  CompletableFuture<User> getUser(String id);

  CompletableFuture<Product> getUserProduct(String userId, String productId);

  CompletableFuture<List<Product>> listUserProducts(ListUserProductsSpec spec);

  CompletableFuture<Long> countUserProducts(ListUserProductsSpec spec);

  CompletableFuture<List<User>> listProductUsers(ListProductUsersSpec spec);

  CompletableFuture<ProductPayload> createProduct(
      CreateProductInput input);

  CompletableFuture<ProductPayload> updateProduct(
      UpdateProductInput input);

  CompletableFuture<ProductPayload> deleteProduct(
      DeleteProductInput input);
}
