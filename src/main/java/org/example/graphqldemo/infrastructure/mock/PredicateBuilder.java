package org.example.graphqldemo.infrastructure.mock;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.example.graphqldemo.core.IntFilter;
import org.example.graphqldemo.core.LogicalFilter;
import org.example.graphqldemo.core.StringFilter;

final class PredicateBuilder {
  private PredicateBuilder() {
  }

  public static <T extends LogicalFilter<T>, R> Predicate<R>
      build(T filter, Function<T, Predicate<R>> factory) {

    if (filter.and != null) {
      List<Predicate<R>> predicates = predicates(filter.and, factory);
      return (value) -> predicates.stream().allMatch(p -> p.test(value));
    }

    if (filter.or != null) {
      List<Predicate<R>> predicates = predicates(filter.or, factory);
      return (value) -> predicates.stream().anyMatch(p -> p.test(value));
    }

    return factory.apply(filter);
  }

  public static Predicate<Integer> build(IntFilter filter) {
    if (filter == null) {
      return (value) -> true;
    }

    if (filter.equals != null) {
      return filter.equals::equals;
    }

    if (filter.lt != null) {
      return (value) -> value < filter.lt;
    }

    if (filter.lte != null) {
      return (value) -> value <= filter.lte;
    }

    if (filter.gt != null) {
      return (value) -> value > filter.gt;
    }

    if (filter.gte != null) {
      return (value) -> value >= filter.gte;
    }

    if (filter.in != null) {
      return (value) -> Arrays.stream(filter.in).anyMatch(value::equals);
    }

    if (filter.not != null) {
      Predicate<Integer> predicate = build(filter.not);
      return (value) -> !predicate.test(value);
    }

    return (value) -> true;
  }

  public static Predicate<String> build(StringFilter filter) {
    if (filter == null) {
      return (value) -> true;
    }

    if (filter.equals != null) {
      return filter.equals::equalsIgnoreCase;
    }

    if (filter.contains != null) {
      return new TextFilterContainsIgnoreCase(filter.contains);
    }

    if (filter.in != null) {
      return (value) -> Arrays.stream(filter.in)
          .anyMatch(value::equalsIgnoreCase);
    }

    if (filter.not != null) {
      Predicate<String> predicate = build(filter.not);
      return (value) -> !predicate.test(value);
    }

    return (value) -> true;
  }

  public static <T extends LogicalFilter<T>, R> List<Predicate<R>>
      predicates(T[] filter, Function<T, Predicate<R>> factory) {
    return Arrays.stream(filter)
        .map(f -> build(f, factory))
        .collect(Collectors.toList());
  }

  private static class TextFilterContainsIgnoreCase
      implements Predicate<String> {
    private final String value;

    public TextFilterContainsIgnoreCase(String value) {
      this.value = value;
    }

    @Override
    public boolean test(String value) {
      final int length = this.value.length();
      if (length == 0) {
        return true;
      }

      for (int i = value.length() - length; i >= 0; i--) {
        if (value.regionMatches(true, i, this.value, 0, length)) {
          return true;
        }
      }

      return false;
    }
  }
}