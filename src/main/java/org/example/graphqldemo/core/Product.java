package org.example.graphqldemo.core;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class Product {
  public String id;
  public String type;
  public String name;
  public Instant updateTime;

  public static Product from(CreateProductInput input) {
    Product p = new Product();
    p.name = input.name;
    p.type = input.type;
    p.updateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    return p;
  }

  public void update(UpdateProductInput input) {
    if (input.name != null) {
      name = input.name;
    }

    updateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
