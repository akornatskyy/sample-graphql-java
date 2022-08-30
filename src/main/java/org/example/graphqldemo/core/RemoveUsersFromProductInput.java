package org.example.graphqldemo.core;

/**
 * The type Remove users from product input.
 */
public class RemoveUsersFromProductInput extends ClientMutationInput {
  public String userId;
  public String[] userIds;
  public String productId;
}
