package life.genny.abn;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.either;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;;

@QuarkusTest
public class EndpointsTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/json?name=gada&size=1")
          .then()
             .statusCode(200)
             .body(
                 // Covers case when the ABN_KEY is no present with the right value
                 either(startsWith("{\"Message\":\"There was a problem completing your request.\""))
                 // Covers case when the ABN_KEY is present 
                 .or(startsWith("{\"Message\":\"\",\"Names\":[{"))
             );
    }

}