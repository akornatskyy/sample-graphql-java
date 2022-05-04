package org.example.graphqldemo.core;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * The type Product.
 */
public final class Product {
  public String id;
  public String type;
  public String name;
  public Instant updateTime;

  /**
   * Create product from create product input.
   */
  public static Product from(CreateProductInput input) {
    Product p = new Product();
    p.name = input.name;
    p.type = input.type;
    p.updateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    return p;
  }

  /**
   * Update product from update product input.
   */
  public void update(UpdateProductInput input) {
    if (input.name != null) {
      name = input.name;
    }

    updateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
