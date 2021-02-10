package life.genny.strategy;

import java.lang.invoke.MethodHandles;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import life.genny.strategy.model.GennyMessage;

@ApplicationScoped
@Named("EmailStrategy")
public class EmailStrategy extends Strategy{

  public static final java.util.logging.Logger log =
          java.util.logging.Logger.getLogger(EmailStrategy.class.getName());

  @Inject
  Mailer mailer;

  @Override
  public void send(GennyMessage gennyMessage) throws Exception{

    String recipient = gennyMessage.getRecipient();
    String emailBody = gennyMessage.getBody();
    String subject ="A simple email from quarkus";

    mailer.send(Mail.withText(recipient, subject, emailBody));

    log.info("-------------EMAIL SENT---------------------");
    log.info("-------------EMAIL RECIPIENT---------------------");
    log.info(recipient);
    log.info("-------------EMAIL BODY---------------------");
    log.info(emailBody);
    log.info("-------------EMAIL PORT---------------------");
   }

 }
