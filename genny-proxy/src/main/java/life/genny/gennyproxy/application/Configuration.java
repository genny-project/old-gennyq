package life.genny.gennyproxy.application;

import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class Configuration {

    @ConfigProperty(name = "quarkus.google.api.port")
    private int port;

    @ConfigProperty(name = "quarkus.google.api.host")
    private String host;


    @Inject
    Vertx vertx;

    @Produces
    public WebClient webClient() {
        return WebClient.create(vertx,
                new WebClientOptions()
                        .setDefaultHost(host)
                        .setDefaultPort(port)
                        .setSsl(true)
                        .setLogActivity(true)
                        .setTrustAll(true));
    }


}