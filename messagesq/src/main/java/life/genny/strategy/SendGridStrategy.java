package life.genny.strategy;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.quarkus.mailer.Mailer;
import life.genny.strategy.model.GennyMessage;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

@ApplicationScoped
@Named("SendGridStrategy")
public class SendGridStrategy extends Strategy {


  public static final java.util.logging.Logger log =
          java.util.logging.Logger.getLogger(SendGridStrategy.class.getName());

  @ConfigProperty(name = "sendgrid.email")
  private String emailSender;

  @ConfigProperty(name = "sendgrid.api.key")
  private String apiKey;

  private static final String END_POINT ="mail/send";

  @Override
  public void send(GennyMessage gennyMessage) throws Exception {

    String recipient = gennyMessage.getRecipient();
    String subject ="A simple email from quarkus";
    String templateId = gennyMessage.getTemplateId();
    Map<String, String> templateData = gennyMessage.getTemplateData();
    System.out.println("Hey I got inside the sendGrid method 256");

    Email from = new Email(emailSender);
    Email to = new Email(recipient);

    SendGrid sg = new SendGrid(apiKey);

    Personalization personalization = new Personalization();

    personalization.addTo(to);

    templateData.forEach((key, val) -> {
      personalization.addDynamicTemplateData(key, val);
    });

    personalization.setSubject(subject);

    com.sendgrid.helpers.mail.Mail mail = new Mail();
    mail.addPersonalization(personalization);
    mail.setTemplateId(templateId);
    mail.setFrom(from);

    Request request = new Request();

      request.setMethod(Method.POST);
      request.setEndpoint(END_POINT);
      request.setBody(mail.build());
      Response response = sg.api(request);
      System.out.println(response.getStatusCode());
      System.out.println(response.getBody());
      System.out.println(response.getHeaders());

   }

 }
