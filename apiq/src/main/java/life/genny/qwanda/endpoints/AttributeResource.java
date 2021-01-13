package life.genny.qwanda.endpoints;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.identity.SecurityIdentity;
import life.genny.models.DataTable;
import life.genny.models.GennyToken;
import life.genny.models.attribute.Attribute;
import life.genny.models.message.QDataAttributeMessage;



@Path("/qwanda/attributes")
@RegisterForReflection
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttributeResource {

    private static final Logger log = Logger.getLogger(AttributeResource.class);

    @ConfigProperty(name = "default.realm", defaultValue = "genny")
    String defaultRealm;


    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    JsonWebToken accessToken;

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

	@POST
	@Consumes("application/json")
	@Transactional
	public Response create(@Context UriInfo uriInfo, @Valid Attribute entity) {
		GennyToken userToken = new GennyToken(accessToken.getRawToken());
		if (entity == null) {
			throw new WebApplicationException("Error in Attribute. ",
					Status.NO_CONTENT);			
		}

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Answer not being created",
					Status.FORBIDDEN);				
		}
		
		entity.id = null;
		entity.persist();
		log.info("Received Attribute ! "+entity);

		URI uri = uriInfo.getAbsolutePathBuilder().path(AttributeResource.class, "findById").build(entity.id);
		return Response.created(uri).build();	}
	
	
	@GET
	@Transactional
	public Response fetchAll() {
	    GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }
		final List<Attribute> entitys = Attribute.listAll();
		Attribute[] atArr = new Attribute[entitys.size()];
//		for (int i =0; i < entitys.size(); i++) {
//            atArr[i] = entitys.get(i); 
//            
//		}
		atArr = entitys.toArray(atArr);
//		log.info("FetchedAll Attributes Array "+atArr.length);
		QDataAttributeMessage msg = new QDataAttributeMessage(atArr);
//		for (Attribute a : msg.getItems()) {
//			log.info(a);
//		}
//		log.info("About to send Attribute Array Message ");
//		Jsonb jsonb = JsonbBuilder.create();
//		String json = jsonb.toJson(msg);
//		log.info("About to send Attribute Array Message as json "+json);
//		
//		Attribute a = new AttributeText("PRI_TEXT","Text");
//		Attribute b = new AttributeText("PRI_TEXT2","Text2");
//		
//		Attribute[] aa = new Attribute[2];
//		aa[0]= a;
//		aa[1] = b;
//		
//		QDataAttributeMessage msg2 = new QDataAttributeMessage(aa);
//		
//		String ja = jsonb.toJson(msg2);
//		log.info("About to send Test Attribute Array Message as json "+ja);
		return Response.status(Status.OK).entity(msg).build();
	}

	@GET
	@Path("/{code}")
	@Transactional
	public Response fetch(@PathParam("code") final String code) {
	    GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

		Attribute attribute = Attribute.find("code", code).firstResult();
		return Response.status(200).entity(attribute).build();
	}
	
	
 

    @Path("/id/{id}")
    @GET
    public Response findById(@PathParam("id") final Long id) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

        Attribute attribute = Attribute.findById(id);
        if (attribute == null) {
            throw new WebApplicationException("Attribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        return Response.status(Status.OK).entity(attribute).build();
    }



    @Path("/{id}")
    @PUT
    @Transactional
    public Response update(@PathParam("id") final Long id, @Valid Attribute entity) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being created",
					Status.FORBIDDEN);				
		}

        Attribute attribute = Attribute.findById(id);
        if (attribute == null) {
            throw new WebApplicationException("Attribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }

        attribute.description = entity.description;
        attribute.updated = LocalDateTime.now();
        attribute.defaultPrivacyFlag = entity.defaultPrivacyFlag;
        attribute.defaultValue = entity.defaultValue;
        attribute.help = entity.help;
        attribute.name = entity.name;
        attribute.placeholder = entity.placeholder;

        attribute.persist();

 
        return Response.status(Status.OK).entity(attribute).build();
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public Response deleteNote(@PathParam("id") final Long id) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

        if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being deleted",
					Status.FORBIDDEN);				
		}

        Attribute attribute = Attribute.findById(id);
        if (attribute == null) {
            throw new WebApplicationException("Attribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        Attribute.deleteById(attribute.id);
        
        return Response.status(Status.OK).build();
    }

    @GET
    @Path("/datatable")
    public DataTable<Attribute> datatable(@QueryParam(value = "draw") int draw, @QueryParam(value = "start") int start,
                                     @QueryParam(value = "length") int length, @QueryParam(value = "search[value]") String searchVal

    ) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());

        DataTable<Attribute> result = new DataTable<>();
        
        if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
 			throw new WebApplicationException("User not recognised. Entityies not being fetched",
 					Status.FORBIDDEN);				
 		}

            searchVal = "";
            result.setDraw(draw);

            PanacheQuery<Attribute> filteredDevice;

            if (searchVal != null && !searchVal.isEmpty()) {
                filteredDevice = Attribute.<Attribute>find("content like :search",
                        Parameters.with("search", "%" + searchVal + "%"));
            } else {
                filteredDevice = Attribute.findAll();
            }

            int page_number = 0;
            if (length > 0) {
                page_number = start / length;
            }
            filteredDevice.page(page_number, length);

            log.info("/datatable: search=[" + searchVal + "],start=" + start + ",length=" + length + ",result#="
                    + filteredDevice.count());

            result.setRecordsFiltered(filteredDevice.count());
            result.setData(filteredDevice.list());
            result.setRecordsTotal(Attribute.count());
   
        return result;

    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        log.info("Attribute Endpoint starting");
 
    }

    @Transactional
    void onShutdown(@Observes ShutdownEvent ev) {
        log.info("Attribute Endpoint Shutting down");
    }
}