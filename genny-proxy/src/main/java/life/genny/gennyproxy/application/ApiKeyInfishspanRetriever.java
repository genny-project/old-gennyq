package life.genny.gennyproxy.application;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named("byInfishspan")
public class ApiKeyInfishspanRetriever implements IApiKeyRetriever {

    @Override
    public String retrieveApiKey(String name, String defaultName){
        throw new IllegalArgumentException("Not Implentmented Yet");
    }

    @Override
    public String getSource() {
        return "From Infishspan";
    }
}
