package life.genny.abn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeEndpointsIT extends EndpointsTest {
  @Test
  public void testHelloEndpoint() {
      given()
        .when().get("/json?name=gada&size=1")
        .then()
           .statusCode(200)
           .body(startsWith("{\"Message\""));
  }
    // Execute the same tests but in native mode.
}