package org.example.graphqldemo.core;

public final class ListUserProductsSpec extends Paging {
  public String userId;
  public UserProductFilters filterBy;
  public UserProductOrder orderBy;
}
