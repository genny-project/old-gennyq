package life.genny.gennyproxy.service;

import life.genny.gennyproxy.repository.UdemyRepository;
import life.genny.gennyproxy.repository.entity.udemy.coursedetails.request.CourseDetailsParams;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class UdemyService {

    @Inject
    private UdemyRepository udemyRepository;

    public String retrieveCourseDetail(String id){
        return udemyRepository.retrieveCourseDetail(id);
    }

    public String retrieveCourseList(CourseDetailsParams courseDetailsParams){
        return udemyRepository.retrieveCourseList(courseDetailsParams);
    }


}
