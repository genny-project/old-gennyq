package life.genny.vertx.eventbus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.invoke.MethodHandles;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.eclipse.microprofile.config.inject.ConfigProperty;


// TODO complate the test case
/**
 * This testing requires MAven Example using plain JavaMail for sending / receiving mails via
 * GreenMail server.
 *
 * http://www.icegreen.com/greenmail/
 * https://www.hascode.com/2012/07/integration-testing-imap-smtp-and-pop3-with-greenmail/
 */
@QuarkusTest
public class NotificationEmailTest {
	
	/**
	 * Stores logger object.
	 */
	protected static final Logger log = org.apache.logging.log4j.LogManager
			.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());


  private GreenMail mailServer;
  //private EmailHelper emailHelper;

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "quarkus.sample.msg")
    private String msg;

  @Test
  public void email_test(){
      mailer.send(Mail.withText("rogerxiaxia@hotmail.com", "A simple email from quarkus", "This is my body.")
              .withHtml("rogerxiaxia@hotmail.com", "A simple email from quarkus", "<p>This is my body.</p>"));
      String actionsArray = "{\"notification\": {\"body\": \"" + "body" + "\",\"title\": \"" + "title"
              + "\"}, \"priority\": \"high\", \"data\": {\"click_action\": \"FLUTTER_NOTIFICATION_CLICK\", \"id\": \"1\", \"status\": \"done\"}, \"to\": \""
              + "userNotificationToken" + "\"}"
              ;

      System.out.println(actionsArray);
      System.out.println(msg);
  }
}
