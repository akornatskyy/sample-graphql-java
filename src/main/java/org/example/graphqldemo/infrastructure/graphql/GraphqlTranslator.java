package org.example.graphqldemo.infrastructure.graphql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import org.example.graphqldemo.core.AddUsersToProductInput;
import org.example.graphqldemo.core.Context;
import org.example.graphqldemo.core.CreateProductInput;
import org.example.graphqldemo.core.DeleteProductInput;
import org.example.graphqldemo.core.ListUserProductsSpec;
import org.example.graphqldemo.core.UpdateProductInput;

final class GraphqlTranslator {
  private static final String INPUT = "input";
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  static ListUserProductsSpec listUserProductsSpecForCount(
      DataFetchingEnvironment env) {
    return listUserProductsSpec(
        env.getLocalContext(),
        env.getExecutionStepInfo().getParent().getArguments());
  }

  static ListUserProductsSpec listUserProductsSpec(
      DataFetchingEnvironment env) {
    return listUserProductsSpec(
        env.getLocalContext(),
        env.getArguments());
  }

  private static ListUserProductsSpec listUserProductsSpec(
      Context context,
      Map<String, Object> arguments) {
    ListUserProductsSpec spec = MAPPER.convertValue(
        arguments,
        ListUserProductsSpec.class);
    spec.userId = context.userId;
    return spec;
  }

  static CreateProductInput createProductInput(
      DataFetchingEnvironment env) {
    CreateProductInput input = MAPPER.convertValue(
        env.getArgument(INPUT), CreateProductInput.class);
    Context context = env.getLocalContext();
    input.userId = context.userId;
    return input;
  }

  static UpdateProductInput updateProductInput(
      DataFetchingEnvironment env) {
    UpdateProductInput input = MAPPER.convertValue(
        env.getArgument(INPUT), UpdateProductInput.class);
    Context context = env.getLocalContext();
    input.userId = context.userId;
    return input;
  }

  static DeleteProductInput deleteProductInput(
      DataFetchingEnvironment env) {
    DeleteProductInput input = MAPPER.convertValue(
        env.getArgument(INPUT), DeleteProductInput.class);
    Context context = env.getLocalContext();
    input.userId = context.userId;
    return input;
  }

  static AddUsersToProductInput addUsersToProductInput(
      DataFetchingEnvironment env) {
    AddUsersToProductInput input = MAPPER.convertValue(
        env.getArgument(INPUT), AddUsersToProductInput.class);
    Context context = env.getLocalContext();
    input.userId = context.userId;
    return input;
  }
}
