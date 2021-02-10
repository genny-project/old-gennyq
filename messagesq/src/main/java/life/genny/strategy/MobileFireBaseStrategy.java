package life.genny.strategy;

import io.vertx.mutiny.ext.web.client.WebClient;
import life.genny.strategy.model.GennyMessage;
import life.genny.strategy.model.firebase.NotificationPayload;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.logging.Logger;


@ApplicationScoped
@Named("MobileFireBaseStrategy")
public class MobileFireBaseStrategy extends Strategy {

  public static final Logger log =
          Logger.getLogger(MobileFireBaseStrategy.class.getName());

    @Inject
    private WebClient webClient;

    @ConfigProperty(name = "quarkus.google.api.host")
    private String host;

    @ConfigProperty(name = "quarkus.google.api.fcm.path")
    private String path;

    @ConfigProperty(name = "quarkus.google.api.port")
    private int port;

    @Override
    public void send(GennyMessage gennyMessage) throws Exception {
        NotificationPayload notificationPayload = new NotificationPayload();
        String apiKey ="";
        String callbackResult =  webClient.post(host, path)
                .port(port)
                .putHeader("Content-Type", MediaType.APPLICATION_JSON)
                .putHeader("Accept", MediaType.APPLICATION_JSON)
                .putHeader("Authorization", "key=" + apiKey)
                .sendJson(notificationPayload)
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
        return ;
    }


//  @Override
//  public void send(GennyMessage gennyMessage) throws Exception {
//       final String userNotificationToken ="";
//      final String title  ="";
//      final String body  ="";
//      String apiKey ="";
//      String firebaseUrl = "https://fcm.googleapis.com/fcm/send";

//
//      CloseableHttpResponse response2 = null;
//      CloseableHttpClient httpclient = HttpClients.createDefault();
//      HttpPost postRequest = new HttpPost(firebaseUrl);
//
//      postRequest.addHeader("Authorization", "key=" + apiKey);
//          postRequest.addHeader("Content-Type", MediaType.APPLICATION_JSON);
//          postRequest.setHeader("Accept", MediaType.APPLICATION_JSON);
//          String actionsArray = "{\"notification\": {\"body\": \"" + body + "\",\"title\": \"" + title
//                  + "\"}, \"priority\": \"high\", \"data\": {\"click_action\": \"FLUTTER_NOTIFICATION_CLICK\", \"id\": \"1\", \"status\": \"done\"}, \"to\": \""
//                  + userNotificationToken + "\"}";
//          log.info(actionsArray);
//
//          try {
//              StringEntity jSonEntity = new StringEntity(actionsArray);
//              postRequest.setEntity(jSonEntity);
//          } catch (UnsupportedEncodingException e) {
//              // TODO Auto-generated catch block
//              e.printStackTrace();
//          }
//          try {
//              response2 = httpclient.execute(postRequest);
//          } catch (IOException e) {
//              // TODO Auto-generated catch block
//              e.printStackTrace();
//          }
//          return response2.toString();
//      }
 // }




}
