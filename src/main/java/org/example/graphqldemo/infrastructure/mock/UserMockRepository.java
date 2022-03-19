package org.example.graphqldemo.infrastructure.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.graphqldemo.core.CreateProductInput;
import org.example.graphqldemo.core.DeleteProductInput;
import org.example.graphqldemo.core.ListProductUsersSpec;
import org.example.graphqldemo.core.ListUserProductsSpec;
import org.example.graphqldemo.core.Product;
import org.example.graphqldemo.core.ProductPayload;
import org.example.graphqldemo.core.UpdateProductInput;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMockRepository implements UserRepository {
  private final ObjectMapper objectMapper;
  private Samples samples;

  public UserMockRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    reset();
  }

  public void reset() {
    try (InputStream is = UserMockRepository.class.getResourceAsStream(
        "/samples.json")) {
      samples = objectMapper.readValue(is, Samples.class);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot read samples.", ex);
    }
  }

  @Override
  public CompletableFuture<List<User>> getUsers(List<String> ids) {
    return CompletableFuture.completedFuture(
        ids.stream()
            .map(id -> samples.users.stream()
                .filter(u -> id.equals(u.id))
                .findFirst().orElse(null))
            .collect(Collectors.toList()));
  }

  @Override
  public CompletableFuture<Product> getUserProduct(
      String userId, String productId) {
    if (!samples.userProducts.get(userId).contains(productId)) {
      throw new IllegalStateException("User product is not found.");
    }

    return CompletableFuture.completedFuture(
        samples.products.stream()
            .filter(p -> productId.equals(p.id))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "Product is not found.")));
  }

  public CompletableFuture<List<Product>> listUserProducts(
      ListUserProductsSpec spec) {
    return CompletableFuture.completedFuture(
        filterUserProducts(spec)
            .collect(Collectors.toList()));
  }

  public CompletableFuture<Long> countUserProducts(
      ListUserProductsSpec spec) {
    return CompletableFuture.completedFuture(
        filterUserProducts(spec)
            .count());
  }

  @Override
  public CompletableFuture<List<String>> listProductUserIds(
      ListProductUsersSpec spec) {
    return getUserProduct(spec.userId, spec.productId)
        .thenApply((product) -> samples.userProducts.entrySet()
            .stream()
            .filter(e -> samples.userProducts
                .getOrDefault(e.getKey(), Collections.emptyList())
                .stream()
                .anyMatch(up -> up.equals(product.id))
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toList()));
  }

  @Override
  public CompletableFuture<ProductPayload> createProduct(
      CreateProductInput input) {
    Product product = Product.from(input);
    product.id = Integer.toString(
        samples.products.stream()
            .map(x -> Integer.parseInt(x.id))
            .max(Integer::compare).orElse(0) + 100);
    samples.products.add(product);
    samples.userProducts.get(input.userId).add(product.id);
    return CompletableFuture.completedFuture(
        new ProductPayload(input, product));
  }

  @Override
  public CompletableFuture<ProductPayload> updateProduct(
      UpdateProductInput input) {
    return getUserProduct(input.userId, input.id)
        .thenApply((product) -> {
          product.update(input);
          return new ProductPayload(input, product);
        });
  }

  @Override
  public CompletableFuture<ProductPayload> deleteProduct(
      DeleteProductInput input) {
    return getUserProduct(input.userId, input.id)
        .thenApply((product) -> {
          samples.products.remove(product);
          return new ProductPayload(input, product);
        });
  }

  private List<String> userProducts(String userId) {
    return samples.userProducts.get(userId);
  }

  private Stream<Product> filterUserProducts(
      ListUserProductsSpec spec) {
    String type = spec.filterBy.type;
    return userProducts(spec.userId)
        .stream()
        .flatMap(productId -> samples.products.stream()
            .filter(p -> p.id.equals(productId)
                         && (type == null || type.equals(p.type))));
  }

  private static class Samples {
    public List<User> users;
    public Map<String, List<String>> userProducts;
    public List<Product> products;
  }
}

