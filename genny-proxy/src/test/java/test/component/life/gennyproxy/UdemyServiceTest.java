package test.component.life.gennyproxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.gennyproxy.repository.entity.abn.AbnSearchResult;
import life.genny.gennyproxy.repository.entity.udemy.coursedetails.request.CourseDetailsParams;
import life.genny.gennyproxy.service.AbnLookupService;
import life.genny.gennyproxy.service.UdemyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class UdemyServiceTest {


    @Inject
    UdemyService udemyService;

    Gson gson;

    @BeforeEach
    public void setup(){
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    public void retrieveCourseDetail_passValidParameter_return200() throws  Exception {
        String courseId = "1424118";
        String courseDetail = udemyService.retrieveCourseDetail(courseId);

        JsonElement je = JsonParser.parseString(courseDetail);
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);
     }

    @Test
    public void retrieveCourseList_passValidParameter_return200() throws  Exception {
        CourseDetailsParams courseDetailsParams = new CourseDetailsParams
                .Builder()
                .withCategory("IT & Software")
                .withPage(1)
                .withOrdering("highest-rated")
                .withSearch("aws")
                .withSubcategory("IT Certification")
                .withRating(3)
                .build();
                String courseDetail = udemyService.retrieveCourseList(courseDetailsParams);

        JsonElement je = JsonParser.parseString(courseDetail);
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);
    }
}
