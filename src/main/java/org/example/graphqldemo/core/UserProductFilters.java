package org.example.graphqldemo.core;

/**
 * The type User product filters.
 */
public final class UserProductFilters
    extends LogicalFilter<UserProductFilters> {

  public StringFilter type;
  public StringFilter name;
  public IntFilter year;
}
