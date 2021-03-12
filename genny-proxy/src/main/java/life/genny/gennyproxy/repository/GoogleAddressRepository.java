package life.genny.gennyproxy.repository;

import io.vertx.mutiny.ext.web.client.WebClient;
import life.genny.gennyproxy.repository.entity.address.Addresses;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class GoogleAddressRepository {

    @Inject
    private WebClient webClient;

    @ConfigProperty(name = "quarkus.google.api.address.path")
    private String addressPath;

    public Addresses retrieveGoogleMap(String address, String apiKey){
        return webClient.get(addressPath)
                .setQueryParam("key", apiKey)
                .setQueryParam("address", address)
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsJson(Addresses.class);
    }




}
