package life.genny.gennyproxy.repository;

import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class GoogleMapRepository {

    @Inject
    private WebClient webClient;

    @ConfigProperty(name = "quarkus.google.api.map.path")
    private String mapPath;


    public String retrieveGoogleMap(String apiKey){
        return webClient.get(mapPath)
                .setQueryParam("key", apiKey)
                .setQueryParam("libraries","places,drawing")
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
    }




}
