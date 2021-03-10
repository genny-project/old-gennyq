package test.blackbox.life.gennyproxy;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class GoogleApiEndpointsTest {

    private static String accessToken;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    Optional<String> keycloakUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    Optional<String> clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    Optional<String> secret;

    @ConfigProperty(name = "quarkus.access.username")
    Optional<String> username;

    @ConfigProperty(name = "quarkus.access.password")
    Optional<String> password;

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @BeforeEach
    public void beforeALL() {

        // RestAssured.baseURI ="https://keycloak.gada.io";
        RestAssured.port = -1;
        String response =  given()
                .log().all()
                .param("grant_type", "password")
                .param("username", username.get())
                .param("password", password.get())
                .param("client_id", clientId.get())
                .param("client_secret", secret.get())
                .when()
                .header("content-type", "application/x-www-form-urlencoded")
                .post(keycloakUrl.get()+ "/protocol/openid-connect/token")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        JSONObject json = new JSONObject(response);
        System.out.println(json.get("access_token"));
        accessToken = (String)json.get("access_token");
    }

    @Test
    public void retrieveGoogleMapApi_passNoParameter_return200() {
        //https://maps.googleapis.com/maps/api/js?key=XXXXX&libraries=places,drawing
        given().auth().oauth2(accessToken)
          .log().all()
          .when()
                .port(8081)
                .get("/googleapi/v1/map")
          .then()
                .log().all()
             .statusCode(200)
             .body(
               containsString("google.maps.Load = function(apiLoad)")
             );
    }

    @Test
    public void retrieveGoogleTimeZoneApi_passValidParameter_return200() {
        //http://localhost:8081/googleapi/v1/timezone?location=-37.913151%2C145.262253&timestamp=1458000000
        String response =  given().auth().oauth2(accessToken)
                .log().all()
                .when() .port(8081)
                .param("location", "-37.913151,145.262253")
                .param("timestamp", 1458000000)
                .get("/googleapi/v1/timezone")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .body()
                .asString();
         assertEquals("Australia/Melbourne",response);


    }

    @Test
    public void retrieveGoogleAddressApi_passValidParameter_return200() {
         String response =  given()
                .log().all().auth().oauth2(accessToken)
                .when()
                 .port(8081)
                 .param("address", "14 Durham Place, Clayton South")
                .get("/googleapi/v1/geocode")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .body()
                .asString();

    }

}