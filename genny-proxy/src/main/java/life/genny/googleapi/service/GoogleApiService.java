package life.genny.googleapi.service;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class GoogleApiService {

    @Inject
    Vertx vertx;

    private WebClient client;


    @ConfigProperty(name = "quarkus.google.api.path")
    private String path;

    @ConfigProperty(name = "quarkus.google.api.port")
    private int port;

    @ConfigProperty(name = "quarkus.google.api.host")
    private String host;

    @PostConstruct
    void init() {
        this.client = WebClient.create(vertx,
                new WebClientOptions()
                        .setDefaultHost(host)
                        .setDefaultPort(port)
                        .setSsl(true)
                        .setLogActivity(true)
                        .setTrustAll(true));
    }

    public String retrieveGoogleMapApi(String apiKey) {

        return client.get(path)
                .setQueryParam("key",apiKey)
                .setQueryParam("libraries","places,drawing")
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
    }
}
