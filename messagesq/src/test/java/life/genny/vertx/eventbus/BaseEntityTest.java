package life.genny.vertx.eventbus;

import com.icegreen.greenmail.util.GreenMail;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.models.entity.BaseEntity;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.lang.invoke.MethodHandles;


// TODO complate the test case

/**
 * This testing requires MAven Example using plain JavaMail for sending / receiving mails via
 * GreenMail server.
 *
 * http://www.icegreen.com/greenmail/
 * https://www.hascode.com/2012/07/integration-testing-imap-smtp-and-pop3-with-greenmail/
 */
@QuarkusTest
public class BaseEntityTest {
	



  @Test
  public void findByCode_baseentityContext_returnSuccess(){
     // BaseEntity baseEntity = BaseEntity.findByCode("APP_20981A5D-28FB5-5D46381C33DA");
     // BaseEntity.listAll().size();
      System.out.println(BaseEntity.count());
  }
}
