package life.genny.gennyproxy.repository;

import io.vertx.mutiny.ext.web.client.WebClient;
import life.genny.gennyproxy.repository.entity.timezone.GoogleTimezone;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class TimezoneRepository {

    @Inject
    private WebClient webClient;

    @ConfigProperty(name = "quarkus.google.api.timezone.path")
    private String timezonePath;

    public GoogleTimezone retrieveGoogleMap(String location, long timestamp, String apiKey){
        //639%20lonsdale%20st

        return webClient.get(timezonePath)
                .setQueryParam("key", apiKey)
                .setQueryParam("location", location)
                .setQueryParam("timestamp", String.valueOf(timestamp))
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsJson(GoogleTimezone.class);
    }




}
