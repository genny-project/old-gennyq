package life.genny.gennyproxy.service;

import java.io.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.google.gson.Gson;
import life.genny.gennyproxy.application.IApiKeyRetriever;
import life.genny.gennyproxy.repository.entity.abn.AbnSearchResult;
import life.genny.gennyproxy.repository.AbnLookupRepository;

@ApplicationScoped
public class AbnLookupService {


  @Inject
  @Named("byEnv")
  //@Named("byInfinispan")
  private IApiKeyRetriever apiKeyRetriever;

  @Inject
  private AbnLookupRepository abnLookupRepository;

  private static final Gson GSON = new Gson();

  public AbnSearchResult retrieveCompanyAbn(String realm, String searchedName, int pageSize) throws UnsupportedEncodingException {

    String abnKey = apiKeyRetriever.retrieveApiKey("ENV_ABN_SEARCH_APIKEY_" + realm, "ENV_ABN_SEARCH_APIKEY_DEFAULT");

    String response =  abnLookupRepository.retrieveCompanyAbn(searchedName, pageSize, abnKey);

    return GSON.fromJson(response, AbnSearchResult.class);

  }

  public String getSource(){
    return apiKeyRetriever.getSource();
  }

}
