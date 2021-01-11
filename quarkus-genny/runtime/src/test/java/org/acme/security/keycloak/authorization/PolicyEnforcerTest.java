package org.acme.security.keycloak.authorization;

import io.quarkus.test.common.QuarkusTestResource;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;


import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import life.genny.models.GennyToken;

@QuarkusTest
@QuarkusTestResource(MySqlServer.class)
@QuarkusTestResource(KeycloakServer.class)
public class PolicyEnforcerTest {

    private static final String KEYCLOAK_REALM = "quarkus";

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    
    @Test
    public void testAccessToken()
    {
    	System.out.println("Starting test");
    	String accessToken = getAccessToken("alice");
    	System.out.println("AccessToken Test="+accessToken);
    	
    	GennyToken gennyToken = new GennyToken(accessToken);
    	
    	System.out.println("Username = "+gennyToken.getString("preferred_username"));
    	System.out.println("KeycloakUrl = "+gennyToken.getKeycloakUrl());
    	System.out.println("Uuid = "+gennyToken.getUniqueId());
    	System.out.println("Realm = "+gennyToken.getRealm());
    }
    
  //  @Test
    public void testAccessUserResource() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);
    }

   // @Test
    public void testAccessAdminResource() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/admin")
                .then()
                .statusCode(403);
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/admin")
                .then()
                .statusCode(403);
        RestAssured.given().auth().oauth2(getAccessToken("admin"))
                .when().get("/api/admin")
                .then()
                .statusCode(200);
    }

   // @Test
    public void testPublicResource() {
        RestAssured.given()
                .when().get("/api/public")
                .then()
                .statusCode(204);
    }

    private String getAccessToken(String userName) {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "backend-service")
                .param("client_secret", "secret")
                .when()
                .post(KeycloakServer.keycloakUrl + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }
}
