package life.genny.gennyproxy.application;

import life.genny.models.GennyToken;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;


@ApplicationScoped
public class AccessTokenParser {

    @Inject
    private JsonWebToken accessToken;

    public String  validateRole(String... validRoles) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());

        String realm = userToken.getRealm();

        System.out.println("realm detected " + realm + " roles  " + userToken.getUserRoles());

        Arrays
                .asList(validRoles)
                .stream()
                .filter(validRole -> userToken.hasRole(validRole))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("User not recognised. Entity not being created", Response.Status.FORBIDDEN));


        return realm.toUpperCase();
    }


}
