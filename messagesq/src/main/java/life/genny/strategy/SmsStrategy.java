package life.genny.strategy;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import life.genny.strategy.model.GennyMessage;
 import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/*
 * Documentations: https://www.twilio.com/docs/libraries/java?utm_source=youtube&utm_medium=video&utm_campaign=youtube_send_sms
 */
@ApplicationScoped
@Named("SmsStrategy")
public class SmsStrategy extends Strategy{



  public static final Logger log =
          Logger.getLogger(SmsStrategy.class.getName());

  // Install the Java helper library from twilio.com/docs/java/install
  @ConfigProperty(name = "twilio.account.sid")
  private String accountSid;

  @ConfigProperty(name = "twilio.auth.token")
  private String authToken;

  @ConfigProperty(name = "twilio.sender.mobile")
  private String senderMobile;

  @Override
  public void send(GennyMessage gennyMessage) {
   Twilio.init(accountSid, authToken);

    Message message =
        Message.creator(
                new com.twilio.type.PhoneNumber(gennyMessage.getRecipient()),
                new com.twilio.type.PhoneNumber(senderMobile),
                gennyMessage.getBody())
            .create();

    log.info("-------------SMS SENT---------------------");
    log.info("By Twilio:" + message.getSid());
    log.info("-------------SMS RECIPIENT---------------------");
    log.info(gennyMessage.getRecipient());
    log.info("-------------SMS BODY---------------------");
    log.info(gennyMessage.getBody());
  }
}
