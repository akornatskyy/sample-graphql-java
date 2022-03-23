package org.example.graphqldemo.infrastructure.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import org.example.graphqldemo.core.UserRepository;
import org.example.graphqldemo.infrastructure.graphql.DataLoaderRegistryFactory;
import org.example.graphqldemo.infrastructure.graphql.GraphQLDataFetchers;
import org.example.graphqldemo.infrastructure.graphql.GraphQLRuntimeWiring;
import org.example.graphqldemo.infrastructure.mock.UserMockRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;

@Configuration
class Config {
  private static final String REPOSITORY_STRATEGY = "repository.strategy";

  @Bean
  public GraphQL graphQL(
      TypeDefinitionRegistry typeRegistry, RuntimeWiring runtimeWiring) {
    return GraphQL
        .newGraphQL(
            new SchemaGenerator()
                .makeExecutableSchema(typeRegistry, runtimeWiring))
        .build();
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public TypeDefinitionRegistry typeRegistry() {
    try (InputStream is = Config.class.getResourceAsStream(
        "/schema.graphql")) {
      return new SchemaParser().parse(is);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot read schema.", ex);
    }
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public RuntimeWiring buildWiring(UserRepository repository) {
    RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
    new GraphQLRuntimeWiring(new GraphQLDataFetchers(repository))
        .addTypeWiring(builder);
    builder.directiveWiring(
        new ValidationSchemaWiring(
            ValidationRules.newValidationRules()
                .onValidationErrorStrategy(
                    OnValidationErrorStrategy.RETURN_NULL)
                .build()));
    return builder.build();
  }

  @Bean
  public DataLoaderRegistryFactory registryFactory(
      UserRepository userRepository) {
    return new DataLoaderRegistryFactory(userRepository);
  }

  @Bean
  public UserRepository userRepository(
      Environment environment,
      ObjectMapper objectMapper) {
    String strategy = environment.getProperty(REPOSITORY_STRATEGY);
    if ("mock".equals(strategy)) {
      return new UserMockRepository(objectMapper);
    }

    throw new IllegalArgumentException(
        String.format("not supported %s: %s", REPOSITORY_STRATEGY, strategy));
  }
}