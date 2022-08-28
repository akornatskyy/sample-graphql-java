package org.example.graphqldemo.infrastructure.mock;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.example.graphqldemo.core.IntFilter;
import org.example.graphqldemo.core.LogicalFilter;
import org.example.graphqldemo.core.StringFilter;

final class PredicateBuilder {
  private static final List<Function<IntFilter, Predicate<Integer>>>
      INT_FILTER_BUILDERS = Arrays.asList(
      (f) -> f == null ? (v) -> true : null,
      (f) -> f.equals != null ? f.equals::equals : null,
      (f) -> f.lt != null ? (v) -> v < f.lt : null,
      (f) -> f.lte != null ? (v) -> v <= f.lte : null,
      (f) -> f.gt != null ? (v) -> v > f.gt : null,
      (f) -> f.gte != null ? (v) -> v >= f.gte : null,
      (f) -> f.in != null
             ? (v) -> Arrays.stream(f.in).anyMatch(v::equals)
             : null,
      (f) -> {
        if (f.not == null) {
          return null;
        }

        Predicate<Integer> predicate = build(f.not);
        return (v) -> !predicate.test(v);
      }
  );

  private static final List<Function<StringFilter, Predicate<String>>>
      STRING_FILTER_BUILDERS = Arrays.asList(
      (f) -> f == null ? (v) -> true : null,
      (f) -> f.equals != null ? f.equals::equals : null,
      (f) -> f.equalsIgnoreCase != null
             ? f.equalsIgnoreCase::equalsIgnoreCase
             : null,
      (f) -> f.contains != null
             ? new TextFilterContainsIgnoreCase(f.contains)
             : null,
      (f) -> {
        if (f.in == null) {
          return null;
        }

        List<String> in = Arrays.asList(f.in);
        return in::contains;
      },
      (f) -> {
        if (f.not == null) {
          return null;
        }

        Predicate<String> predicate = build(f.not);
        return (v) -> !predicate.test(v);
      }
  );

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
    return INT_FILTER_BUILDERS.stream()
        .map((b) -> b.apply(filter))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse((value) -> true);
  }

  public static Predicate<String> build(StringFilter filter) {
    return STRING_FILTER_BUILDERS.stream()
        .map((b) -> b.apply(filter))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse((value) -> true);
  }

  private static <T extends LogicalFilter<T>, R> List<Predicate<R>>
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