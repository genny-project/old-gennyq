package life.genny.abn;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/json")
public class Endpoints {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public AbnSearchResult hello(@QueryParam String name,@QueryParam int size) {
    AbnSearchResult searchByName = null;
    try {
      searchByName =
          AbnLookup.searchByName(name, size);
      System.out.println(searchByName);
    } catch (URISyntaxException 
        | IOException 
        | SAXException 
        | ParserConfigurationException 
        | FactoryConfigurationError
        | SecurityException
        | IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return searchByName;
  }
}
