package life.genny.gennyproxy.resource;

import life.genny.gennyproxy.application.AccessTokenParser;
import life.genny.gennyproxy.model.address.AddressResp;
import life.genny.gennyproxy.repository.entity.udemy.coursedetails.request.CourseDetailsParams;
import life.genny.gennyproxy.service.GoogleApiService;
import life.genny.gennyproxy.service.UdemyService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/udemyapi")
public class UdemyApiResource {

  @Inject
  private UdemyService udemyService;

  @Inject
  private AccessTokenParser accessTokenParser;

  @GET
  @Path("v1/course/detail/{id}")
  @NoCache
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveCourseDetail(@PathParam("id") String id) {

    String realm = accessTokenParser.validateRole("user", "superadmin");

    String resp = udemyService.retrieveCourseDetail(id);

    return Response.ok(resp, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("v1/course/list")
  @NoCache
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveCourseList(@QueryParam("category") String category,
                                     @QueryParam("subcategory") String subcategory,
                                     @QueryParam("ordering") String ordering,
                                     @QueryParam("rating") Integer rating,
                                     @QueryParam("page") Integer page,
                                     @QueryParam("search") String search
                                     ) {
    String realm = accessTokenParser.validateRole("user", "superadmin");

    // could be better replace with custom validation annoation
    if(page == null || page < 1){
       throw new IllegalArgumentException("page number should not be empty or greater than 1");
    }

    CourseDetailsParams courseDetailsParams = new CourseDetailsParams.Builder()
            .withCategory(category)
            .withPage(page)
            .withOrdering(ordering)
            .withSearch("aws")
            .withSubcategory(subcategory)
            .withRating(rating)
            .build();
    String resp  = udemyService.retrieveCourseList(courseDetailsParams);

    return Response.ok(resp, MediaType.APPLICATION_JSON).build();

  }

}
