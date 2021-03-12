package life.genny.gennyproxy.repository;

import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class AbnLookupRepository {

    @Inject
    private WebClient webClient;

    private static final String callbackName = "c";

    @ConfigProperty(name = "quarkus.api.abn.search.host")
    private String host;

    @ConfigProperty(name = "quarkus.api.abn.search.path")
    private String abnSearchPath;

    @ConfigProperty(name = "quarkus.api.abn.search.port")
    private int port;

    public String  retrieveCompanyAbn(String searchedName, int pageSize, String abnKey){
        String callbackResult =  webClient.get(host, abnSearchPath)
                .port(port)
                .setQueryParam("callback", callbackName)
                .setQueryParam("name", searchedName)
                .setQueryParam("maxResults", String.valueOf(pageSize))
                .setQueryParam("guid", abnKey)
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
        return getCallbackJson(callbackResult);
    }

    private String getCallbackJson(String callbackResult) {
        return callbackResult.substring(callbackName.length() + 1, callbackResult.length() - 1);
    }


}
