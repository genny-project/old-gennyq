package life.genny.quarkus.quarkus.nest.deployment;

import life.genny.nest.endpoints.ExampleResource;
import life.genny.nest.endpoints.GreetingServlet;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;

class QuarkusNestProcessor {

    private static final String FEATURE = "quarkus-nest";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    ServletBuildItem createServlet() { 
      ServletBuildItem servletBuildItem = ServletBuildItem.builder("crowtech-nest", GreetingServlet.class.getName())
        .addMapping("/greeting")
        .build(); 
      return servletBuildItem;
    }

//    @BuildStep
//    ServletBuildItem createEcho() { 
//      ServletBuildItem servletBuildItem = ServletBuildItem.builder("crowtech-nest-echo", ExampleResource.class.getName())
//        .addMapping("/echo")
//        .build(); 
//      return servletBuildItem;
//    }
}
