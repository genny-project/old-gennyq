package test.nativeimage.life.gennyproxy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;

import test.blackbox.life.gennyproxy.AbnLookupEndpointsTest;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeEndpointsIT extends AbnLookupEndpointsTest {
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