package team.quad;

import com.github.javafaker.Faker;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import io.quarkus.test.junit.QuarkusTest;

//import life.genny.models.entity.BaseEntity;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import java.io.Serializable;

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
        BaseEntityReq baseEntity = new BaseEntityReq();
         given()
            .contentType("application/json")
          .log().all()
          .body(baseEntity)
            .when().post("/join").then().log().all()
                .statusCode(200);
    }

    @Test
    public void testJoinGroupResponse() {
        BaseEntityReq baseEntity = new BaseEntityReq();
        BaseEntityResp result =
              given()
                .contentType("application/json")
                .log().all()
                .body(baseEntity)
                .when()
                      .post("/join")
                      .as(BaseEntityResp.class);


//        //System.out.println(result);
//
         assertEquals("PRI_GENNY", result.getCode());
         assertEquals("test", result.getName());
    }




}
