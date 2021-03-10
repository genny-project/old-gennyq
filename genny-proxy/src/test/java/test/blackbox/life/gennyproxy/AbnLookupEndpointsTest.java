package test.blackbox.life.gennyproxy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.either;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
public class AbnLookupEndpointsTest {

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
    public void retrieveCompanyAbn_passNoParameter_return200() {
//        given()
//          .when().get("/json?name=gada&size=1")
//          .then()
//             .statusCode(200)
//             .body(
//                 // Covers case when the ABN_KEY is no present with the right value
//                 either(startsWith("{\"Message\":\"There was a problem completing your request.\""))
//                 // Covers case when the ABN_KEY is present
//                 .or(startsWith("{\"Message\":\"\",\"Names\":[{"))
//             );


          given().auth().oauth2(accessToken)
                .log().all()
                .when()
                  .port(8081)
                .get("/json?name=gada&size=1")
                .then()
                .log().all()
                .statusCode(200)

                .body(
                 // Covers case when the ABN_KEY is no present with the right value
                 either(startsWith("{\"Message\":\"There was a problem completing your request.\""))
                 // Covers case when the ABN_KEY is present
                 .or(startsWith("{\"message\":\"\",\"names\":[{"))
             );

       // System.out.println(response);
    }

}