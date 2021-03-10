package life.genny.gennyproxy.resource;

import life.genny.gennyproxy.application.AccessTokenParser;
import life.genny.gennyproxy.model.address.AddressResp;
import life.genny.gennyproxy.service.GoogleApiService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import life.genny.gennyproxy.repository.entity.address.Addresses;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/googleapi")
public class GoogleApiResource {

  @Inject
  private GoogleApiService googleApiService;

  @Inject
  private AccessTokenParser accessTokenParser;

  @GET
  @Path("v1/map")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveGoogleMapApi() {

    String realm = accessTokenParser.validateRole("user", "superadmin");

    String respGoogleMapJs = googleApiService.retrieveGoogleMapApi(realm);

    return Response.ok(respGoogleMapJs, MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Path("v1/timezone")
  @NoCache
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveGoogleTimeZoneApi(@QueryParam("location") String location, @QueryParam("timestamp") long timestamp ) {

    String realm = accessTokenParser.validateRole("user", "superadmin");

    String timeZoneId = googleApiService.retrieveGoogleTimeZoneApi(realm, location, timestamp);

    return Response.ok(timeZoneId, MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Path("v1/geocode")
  @NoCache
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveGoogleAddressApi(@QueryParam("address") String address) {

    String realm = accessTokenParser.validateRole("user", "superadmin");

    List<AddressResp> addresses = googleApiService.retrieveGoogleAddressApi(realm, address);

    return Response.ok(addresses, MediaType.APPLICATION_JSON).build();

  }



}
