package life.genny.gennyproxy.resource;

import life.genny.gennyproxy.application.AccessTokenParser;
import life.genny.gennyproxy.repository.entity.abn.AbnSearchResult;
import life.genny.gennyproxy.service.AbnLookupService;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/json")
public class AbnResource {

  @Inject
  private AbnLookupService abnLookupService;

  @Inject
  private AccessTokenParser accessTokenParser;


  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveCompanyAbn(@QueryParam("name") String name, @QueryParam("size") int size) throws Exception {

    String realm = accessTokenParser.validateRole("user", "superadmin");

    AbnSearchResult abnSearchResult = abnLookupService.retrieveCompanyAbn(realm, name, size);

    return Response.ok(abnSearchResult, MediaType.APPLICATION_JSON).build();
  }


}
