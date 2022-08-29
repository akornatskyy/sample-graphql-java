package org.example.graphqldemo.core;

/**
 * The type Add users to product input.
 */
public class AddUsersToProductInput extends ClientMutationInput {
  public String userId;
  public String[] userIds;
  public String productId;
}
