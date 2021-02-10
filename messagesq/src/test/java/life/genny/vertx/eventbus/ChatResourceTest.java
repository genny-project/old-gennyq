package life.genny.vertx.eventbus;

import io.quarkus.test.junit.QuarkusTest;

//import life.genny.models.entity.BaseEntity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@QuarkusTest
public class ChatResourceTest {

//    @Test
//    public void testUserEquality() {
//        User user1 = new User("john@quad.team", "John");
//        User user2 = new User("john@quad.team", "John");
//
//        assertEquals(user1, user2);
//        assertNotSame(user1, user2);
//    }

    @Test
    public void testJoinGroupEndpointExists() {
        MessageReq messageReq = new MessageReq();
        messageReq.baseentitycode = "APP_20981A5D-28B7-457D-8FB5-5D46381C33DA";


        given()
            .contentType("application/json")
          .log().all()
          .body(messageReq)
            .when().post("/join").then().log().all()
                .statusCode(200);
    }

    @Test
    public void testJoinGroupResponse() {
        MessageReq messageReq = new MessageReq();
        messageReq.baseentitycode = "APP_20981A5D-28B7-457D-8FB5-5D46381C33DA";

        BaseEntityResp result =
              given()
                .contentType("application/json")
                .log().all()
                .body(messageReq)
                .when()
                      .post("/join")
                      .as(BaseEntityResp.class);


//        //System.out.println(result);
//
        // assertEquals("PRI_GENNY", result.getCode());
         assertEquals("test", result.getName());
    }




}
