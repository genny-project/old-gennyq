package life.genny.gennyproxy.application;

public interface IApiKeyRetriever {

    String retrieveApiKey(String name, String defaultName);

    String getSource();
}
