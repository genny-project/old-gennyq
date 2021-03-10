package test.blackbox.life.gennyproxy;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class UdemyApiEndpointsTest {

    private static String accessToken;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    Optional<String> keycloakUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    Optional<String> clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    Optional<String> secret;

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
                .param("username", "test1234@gmail.com")
                .param("password", "alice")
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
    public void retrieveCourseList_passValidParameter_return200() {
         given().auth().oauth2(accessToken)
          .log().all()
          .when()
                  .param("category","IT & Software")
                  .param("subcategory","IT Certification")
                 .param("ordering","highest-rated")
                 .param("rating","3")
                 .param("page","1")
                 .param("search","aws")
                .port(8081)
                .get("/udemyapi/v1/course/list")
          .then()
                .log().all()
             .statusCode(200)
            .extract()
                .body()
                .asString();
//             .body(
//               containsString("google.maps.Load = function(apiLoad)")
//             );
    }

    @Test
    public void retrieveCourseDetail_passValidParameter_return200() {
         String response =  given().auth().oauth2(accessToken)
                .log().all()
                .when() .port(8081)
                .get("/udemyapi/v1/course/detail/1424118")
                .then()
                .log().all()
                .statusCode(200)
                 .body(
               containsString("1424118")
             )
                .extract()
                .body()
                .asString();


    }



}