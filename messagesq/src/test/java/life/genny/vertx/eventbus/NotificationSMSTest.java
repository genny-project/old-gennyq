package life.genny.vertx.eventbus;

import io.quarkus.test.junit.QuarkusTest;
import life.genny.strategy.StrategyContext;
import life.genny.strategy.model.GennyMessage;
import life.genny.strategy.model.QBaseMSGMessageType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


// TODO complate the test case

/**
 * This testing requires MAven Example using plain JavaMail for sending / receiving mails via
 * GreenMail server.
 *
 * http://www.icegreen.com/greenmail/
 * https://www.hascode.com/2012/07/integration-testing-imap-smtp-and-pop3-with-greenmail/
 */
@QuarkusTest
public class NotificationSMSTest {

	@ConfigProperty(name = "sms.test", defaultValue = "false")
	String smsTest;

	
    @Inject
    private StrategyContext strategyContext;

    @Test
    public void sendShouldSetTheRightText() throws Exception {

    	if ("TRUE".equalsIgnoreCase(smsTest)) {
    		
    		System.out.println("Testing SMS");
    		
    		//https://fakenumber.org/australia/mobile
    		GennyMessage gennyMessage = new GennyMessage();
    		gennyMessage.setBody("this is test message from X");
    		gennyMessage.setRecipient("+61491570156");
    		gennyMessage.setQBaseMSGMessageType(QBaseMSGMessageType.SMS);

    		strategyContext.execute(gennyMessage);

    		System.out.println("done23");
    	}

    }
}
