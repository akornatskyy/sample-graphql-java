package org.example.graphqldemo.core;

/**
 * The type Product payload.
 */
public class ProductPayload {
  public final String clientMutationId;
  public final Product product;

  public ProductPayload(ClientMutationInput input, Product product) {
    this.clientMutationId = input.clientMutationId;
    this.product = product;
  }
}
