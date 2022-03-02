package org.example.graphqldemo.infrastructure.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.example.graphqldemo.core.UserRepository;
import org.example.graphqldemo.infrastructure.graphql.GraphQLDataFetchers;
import org.example.graphqldemo.infrastructure.graphql.GraphQLRuntimeWiring;
import org.example.graphqldemo.infrastructure.mock.UserMockRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
class Config {
  @Bean
  public UserRepository userRepository(ObjectMapper objectMapper) {
    return new UserMockRepository(objectMapper);
  }

  @Bean
  public GraphQLDataFetchers dataFetchers(UserRepository repository) {
    return new GraphQLDataFetchers(repository);
  }

  @Bean
  public GraphQL graphQL(RuntimeWiring runtimeWiring) throws IOException {
    TypeDefinitionRegistry typeRegistry;
    try (InputStream is = Config.class.getResourceAsStream("/schema.graphql")) {
      typeRegistry = new SchemaParser().parse(is);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot read samples.", ex);
    }

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(
        typeRegistry, runtimeWiring);
    return GraphQL.newGraphQL(graphQLSchema).build();
  }

  @Bean
  public RuntimeWiring buildWiring(GraphQLDataFetchers graphQLDataFetchers) {
    return GraphQLRuntimeWiring.buildWiring(graphQLDataFetchers);
  }
}