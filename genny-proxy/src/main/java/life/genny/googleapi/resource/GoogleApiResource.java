package life.genny.googleapi.resource;

import io.netty.util.internal.StringUtil;
import life.genny.googleapi.service.GoogleApiService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/googleapi")
public class GoogleApiResource {

  @Inject
  private GoogleApiService googleApiService;

  @GET
  @Path("v1/map")
  @Produces(MediaType.APPLICATION_JSON)
  public String getGoogleMapApi() throws Exception {
    String apiKey = System.getenv("ENV_GOOGLE_MAPS_APIKEY");

    if(StringUtil.isNullOrEmpty(apiKey)){
       throw new Exception("Google Map API key is not set in build.sh or current IDE env");
    }

    return googleApiService.retrieveGoogleMapApi(apiKey);
  }
}
