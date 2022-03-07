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
  private final Samples samples;

  public UserMockRepository(ObjectMapper objectMapper) {
    try (InputStream is = UserMockRepository.class.getResourceAsStream(
        "/samples.json")) {
      samples = objectMapper.readValue(is, Samples.class);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot read samples.", ex);
    }
  }

  public CompletableFuture<User> getUser(String id) {
    return CompletableFuture.completedFuture(
        samples.users.stream()
            .filter(u -> u.id.equals(id))
            .findFirst()
            .get());
  }

  @Override
  public CompletableFuture<Product> getUserProduct(
      String userId, String productId) {
    if (!samples.userProducts.get(userId).contains(productId)) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.completedFuture(
        samples.products.stream()
            .filter(p -> productId.equals(p.id))
            .findFirst()
            .get());
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
  public CompletableFuture<List<User>> listProductUsers(
      ListProductUsersSpec spec) {
    return getUserProduct(spec.userId, spec.productId)
        .thenApply((product) -> samples.userProducts.entrySet()
            .stream()
            .filter(e -> samples.userProducts
                .getOrDefault(e.getKey(), Collections.emptyList())
                .stream()
                .anyMatch(up -> up.equals(product.id))
            )
            .flatMap(e -> samples.users
                .stream()
                .filter(u -> u.id.equals(e.getKey())))
            .collect(Collectors.toList()));
  }

  @Override
  public CompletableFuture<ProductPayload> createProduct(
      CreateProductInput input) {
    Product product = Product.from(input);
    Integer id = samples.products.stream()
                     .map(x -> Integer.parseInt(x.id))
                     .max(Integer::compare).get() + 100;
    product.id = id.toString();
    samples.products.add(product);
    samples.userProducts.get(input.userId).add(product.id);

    return CompletableFuture.completedFuture(
        new ProductPayload(input, product));
  }

  @Override
  public CompletableFuture<ProductPayload> updateProduct(
      UpdateProductInput input) {
    Product product = samples.products.stream()
        .filter(p -> input.id.equals(p.id))
        .findFirst()
        .get();
    product.update(input);
    return CompletableFuture.completedFuture(
        new ProductPayload(input, product));
  }

  @Override
  public CompletableFuture<ProductPayload> deleteProduct(
      DeleteProductInput input) {
    if (!samples.userProducts.get(input.userId).remove(input.id)) {
      return CompletableFuture.completedFuture(
          new ProductPayload(input, null));
    }

    Product product = samples.products.stream()
        .filter(p -> input.id.equals(p.id))
        .findFirst()
        .get();
    samples.products.remove(product);
    return CompletableFuture.completedFuture(
        new ProductPayload(input, product));
  }

  private Stream<Product> filterUserProducts(
      ListUserProductsSpec spec) {
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