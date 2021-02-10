package life.genny.application;



import life.genny.strategy.*;
import life.genny.strategy.model.QBaseMSGMessageType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import io.vertx.mutiny.core.Vertx;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class Configuration {

    @Inject
    Vertx vertx;

    @Produces
    public WebClient webClient() {
        return WebClient.create(vertx,
                new WebClientOptions()
                        .setSsl(true)
                        .setLogActivity(true)
                        .setTrustAll(true));
    }

    @Produces
    public Map<QBaseMSGMessageType, Strategy> strategies() {
        Map<QBaseMSGMessageType, Strategy> strategyMap = new HashMap<>();

        // add more media and strategies
        strategyMap.put(QBaseMSGMessageType.SMS, CDI.current().select(SmsStrategy.class).get());
        strategyMap.put(QBaseMSGMessageType.EMAIL, CDI.current().select(EmailStrategy.class).get());
        strategyMap.put(QBaseMSGMessageType.SEND_GRID, CDI.current().select(SendGridStrategy.class).get());
        strategyMap.put(QBaseMSGMessageType.MOBILE_FIREBASE, CDI.current().select(MobileFireBaseStrategy.class).get());


        return  strategyMap;
    }


}