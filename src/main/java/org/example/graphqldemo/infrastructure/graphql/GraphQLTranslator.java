package org.example.graphqldemo.infrastructure.graphql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.ListUserProductsSpec;

import java.util.Map;

final class GraphQLTranslator {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  static ListUserProductsSpec listUserProductsSpec(
      DataFetchingEnvironment env
  ) {
    return listUserProductsSpec(
        env.getLocalContext(),
        env.getArguments());
  }

  static ListUserProductsSpec listUserProductsSpecForCount(
      DataFetchingEnvironment env
  ) {
    return listUserProductsSpec(
        env.getLocalContext(),
        env.getExecutionStepInfo().getParent().getArguments());
  }

  private static ListUserProductsSpec listUserProductsSpec(
      Context context,
      Map<String, Object> arguments
  ) {
    ListUserProductsSpec spec = MAPPER.convertValue(
        arguments,
        ListUserProductsSpec.class);
    spec.userId = context.userId;
    return spec;
  }
}
