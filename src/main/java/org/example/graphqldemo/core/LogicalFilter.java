package org.example.graphqldemo.core;

/**
 * The type Logical filter.
 *
 * @param <T> the type parameter
 */
public abstract class LogicalFilter<T extends LogicalFilter<T>> {
  public T[] and;
  public T[] or;
}
