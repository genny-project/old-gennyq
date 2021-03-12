package life.genny.gennyproxy.repository;

import io.vertx.mutiny.ext.web.client.WebClient;
import life.genny.gennyproxy.repository.entity.udemy.coursedetails.request.CourseDetailsParams;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class UdemyRepository {

    @Inject
    private WebClient webClient;

    @ConfigProperty(name = "quarkus.udemy.api.host")
    private String host;

    @ConfigProperty(name = "quarkus.udemy.api.course.detail.path")
    private String courseDetailPath;

    @ConfigProperty(name = "quarkus.udemy.api.course.list.path")
    private String courseListPath;

    @ConfigProperty(name = "quarkus.udemy.api.course.list.pagesize")
    private int courseListPageSize;

    @ConfigProperty(name = "quarkus.udemy.api.course.token")
    private String courseToken;

    @ConfigProperty(name = "quarkus.udemy.api.port")
    private int port;

    public String retrieveCourseDetail(String id){
         return webClient
                .get(host, String.format(courseDetailPath, id))
                .putHeader("Authorization",String.format("Basic %s", courseToken))
                .port(port)
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
    }

    public String retrieveCourseList(CourseDetailsParams courseDetailsParams){
        return webClient
                .get(host, courseListPath)
                .putHeader("Authorization",String.format("Basic %s", courseToken))
                .setQueryParam("category", courseDetailsParams.getCategory())
                .setQueryParam("subcategory",courseDetailsParams.getSubcategory())
                .setQueryParam("ordering", courseDetailsParams.getOrdering())
                .setQueryParam("rating", String.valueOf(courseDetailsParams.getRating()))
                .setQueryParam("page", String.valueOf(courseDetailsParams.getPage()))
                .setQueryParam("page_size",String.valueOf(courseListPageSize))
                .setQueryParam("search", courseDetailsParams.getSearch())
                .port(port)
                .send()
                .await()
                .atMost(Duration.ofSeconds(15))
                .bodyAsString();
    }



}
