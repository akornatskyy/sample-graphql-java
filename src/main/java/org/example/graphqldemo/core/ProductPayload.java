package org.example.graphqldemo.core;

public class ProductPayload {
  public final String clientMutationId;
  public final Product product;

  public ProductPayload(ClientMutationInput input) {
    this(input, null);
  }

  public ProductPayload(ClientMutationInput input, Product product) {
    this.clientMutationId = input.clientMutationId;
    this.product = product;
  }
}
