package org.example.graphqldemo.infrastructure.graphql;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.example.graphqldemo.core.User;
import org.example.graphqldemo.core.UserRepository;

/**
 * The type Data loader registry factory.
 */
public final class DataLoaderRegistryFactory {
  private final UserRepository userRepository;

  public DataLoaderRegistryFactory(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Create data loader registry.
   */
  public DataLoaderRegistry create() {
    return new DataLoaderRegistry()
        .register(
            DataLoaderNames.USERS,
            DataLoaderFactory.newDataLoader(this::getUsers));
  }

  private CompletableFuture<List<User>> getUsers(List<String> ids) {
    return userRepository.getUsers(ids);
  }
}
