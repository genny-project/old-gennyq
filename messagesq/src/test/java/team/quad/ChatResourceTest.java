package team.quad;

import io.quarkus.test.junit.QuarkusTest;

import life.genny.qwanda.entity.BaseEntity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
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
        BaseEntity baseEntity = new BaseEntity();
         given()
            .contentType("application/json")
          .log().all()
          .body(baseEntity)
            .when().post("/join").then().log().all()
                .statusCode(200);
    }

    @Test
    public void testJoinGroupResponse() {
        BaseEntity baseEntity = new BaseEntity();
         BaseEntity result =
              given()
                .contentType("application/json")
                .log().all()
                .body(baseEntity)
                .when().post("/join").then().log().all().extract()
                .as(BaseEntity.class);

        assertEquals("PRI_GENNY", result.code);
        assertEquals("test", result.name);
    }

}
