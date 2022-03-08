package org.example.graphqldemo.infrastructure.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.graphqldemo.core.UserRepository;
import org.example.graphqldemo.infrastructure.mock.UserMockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GraphQLControllerTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WebTestClient webClient;

  @BeforeMethod
  public void beforeEach() {
    ((UserMockRepository) userRepository).reset();
  }

  @Test(dataProvider = "data")
  public void test(TestInput input) {
    WebTestClient.ResponseSpec response = post(input.toRequest())
        .header("X-User", input.userId != null ? input.userId : "1000")
        .exchange();

    response
        .expectStatus().isOk()
        .expectBody()
        .json(input.result, true);
  }

  @DataProvider
  public Object[][] data() throws IOException {
    try (InputStream is = TestData.class.getResourceAsStream(
        "/graphql-test-data.yaml")) {
      return new ObjectMapper(new YAMLFactory())
          .readValue(is, TestData.class)
          .inputs.stream()
          .map(d -> new Object[] {d})
          .toArray(Object[][]::new);
    }
  }
  
  private WebTestClient.RequestHeadersSpec<?> post(GraphQLRequest request) {
    return webClient.post()
        .uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), GraphQLRequest.class);
  }

  private static class GraphQLRequest {
    public String query;
    public Map<String, Object> variables;
  }

  private static class TestData {
    public List<TestInput> inputs;
  }

  private static class TestInput {
    public String title;
    public String userId;
    public String query;
    public Map<String, Object> variables;
    public String result;

    public GraphQLRequest toRequest() {
      GraphQLRequest request = new GraphQLRequest();
      request.query = query;
      request.variables = variables;
      return request;
    }

    @Override
    public String toString() {
      return title + ", query='" + query + "', result='" + result + '\'';
    }
  }
}