package org.example.graphqldemo.core;

/**
 * The type List user products spec.
 */
public final class ListUserProductsSpec extends Paging {
  public String userId;
  public UserProductFilters filterBy;
  public UserProductOrder orderBy;
}
