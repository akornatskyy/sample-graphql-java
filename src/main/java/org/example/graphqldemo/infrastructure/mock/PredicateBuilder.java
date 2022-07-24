package org.example.graphqldemo.infrastructure.mock;

import java.util.function.Predicate;
import org.example.graphqldemo.core.TextFilter;
import org.example.graphqldemo.core.TextFilterCondition;

final class PredicateBuilder {
  private PredicateBuilder() {
  }

  public static Predicate<String> build(TextFilter filter) {
    if (filter == null) {
      return (s) -> true;
    }

    String value = filter.value;
    if (value == null || value.isEmpty()) {
      return (s) -> true;
    }

    if (filter.condition == TextFilterCondition.EQUALS) {
      return value::equalsIgnoreCase;
    }

    return new TextFilterContainsIgnoreCase(value);
  }

  private static class TextFilterContainsIgnoreCase
      implements Predicate<String> {
    private final String value;

    public TextFilterContainsIgnoreCase(String value) {
      this.value = value;
    }

    @Override
    public boolean test(String s) {
      final int length = this.value.length();
      if (length == 0) {
        return true;
      }

      for (int i = s.length() - length; i >= 0; i--) {
        if (s.regionMatches(true, i, this.value, 0, length)) {
          return true;
        }
      }

      return false;
    }
  }
}