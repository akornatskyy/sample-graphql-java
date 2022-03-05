package org.example.graphqldemo.infrastructure.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.ListUserProductsSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;
import org.example.graphqldemo.infrastructure.graphql.GraphQLDataFetchers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMockRepository implements UserRepository {
  private final Samples samples;

  public UserMockRepository(ObjectMapper objectMapper) {
    try (InputStream is = UserMockRepository.class.getResourceAsStream(
        "/samples.json")) {
      samples = objectMapper.readValue(is, Samples.class);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot read samples.", ex);
    }
  }

  public CompletableFuture<User> findUserById(String id) {
    return CompletableFuture.completedFuture(
        samples.users.stream()
            .filter(u -> u.id.equals(id))
            .findFirst()
            .get());
  }

  public CompletableFuture<List<Product>> listUserProducts(ListUserProductsSpec spec) {
    return CompletableFuture
        .completedFuture(
            filterUserProducts(spec)
                .collect(Collectors.toList()));
  }

  public CompletableFuture<Long> countUserProducts(ListUserProductsSpec spec) {
    return CompletableFuture
        .completedFuture(
            filterUserProducts(spec)
                .count());
  }

  @Override
  public CompletableFuture<List<User>> listProductUsers(ListProductUsersSpec spec) {
    return CompletableFuture.completedFuture(
        samples.userProducts.entrySet()
            .stream()
            .filter(e -> samples.userProducts
                .getOrDefault(e.getKey(), Collections.emptyList())
                .stream()
                .anyMatch(up -> up.equals(spec.productId))
            )
            .flatMap(e -> samples.users
                .stream()
                .filter(u -> u.id.equals(e.getKey())))
            .collect(Collectors.toList()));
  }

  private Stream<Product> filterUserProducts(ListUserProductsSpec spec) {
    String type = spec.filterBy.type;
    return samples.userProducts.get(spec.userId)
        .stream()
        .flatMap(productId -> samples.products.stream()
            .filter(p -> p.id.equals(productId)
                         && (type == null || type.equals(p.type))));
  }
}

class Samples {
  public List<User> users;
  public Map<String, List<String>> userProducts;
  public List<Product> products;
}