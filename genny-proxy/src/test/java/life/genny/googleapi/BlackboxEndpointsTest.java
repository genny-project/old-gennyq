package life.genny.googleapi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class BlackboxEndpointsTest {

   // @Test
    public void getGoogleMapApi_passNoParameter_return200() {
        //https://maps.googleapis.com/maps/api/js?key=XXXXX&libraries=places,drawing
        given()
          .log().all()
          .when().get("/googleapi/v1/map")
          .then()
                .log().all()
             .statusCode(200)
             .body(
               containsString("google.maps.Load = function(apiLoad)")
             );
    }

}