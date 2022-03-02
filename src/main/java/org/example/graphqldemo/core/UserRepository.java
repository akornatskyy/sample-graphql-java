package org.example.graphqldemo.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
  CompletableFuture<User> findUserById(String id);
  CompletableFuture<List<Product>> listUserProducts(ListUserProductsSpec spec);
  CompletableFuture<Long> countUserProducts(ListUserProductsSpec spec);
  CompletableFuture<List<User>> listProductUsers(ListProductUsersSpec spec);
}
