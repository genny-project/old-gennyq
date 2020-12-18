package life.genny.qwanda.endpoints;

import java.net.URI;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import life.genny.qwanda.DataTable;
import life.genny.qwanda.GennyToken;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.message.QDataEntityAttributeMessage;

//
//import java.net.URI;
//import java.util.List;
//
//import javax.enterprise.event.Observes;
//import javax.inject.Inject;
//import javax.transaction.Transactional;
//import javax.validation.Valid;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.OPTIONS;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;
//import javax.ws.rs.core.UriInfo;
//
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.eclipse.microprofile.jwt.JsonWebToken;
//import org.jboss.logging.Logger;
//import org.jboss.resteasy.annotations.jaxrs.PathParam;
//
//import io.quarkus.hibernate.orm.panache.PanacheQuery;
//import io.quarkus.panache.common.Parameters;
//import io.quarkus.runtime.ShutdownEvent;
//import io.quarkus.runtime.StartupEvent;
//import io.quarkus.runtime.annotations.RegisterForReflection;
//import io.quarkus.security.identity.SecurityIdentity;
//import life.genny.qwanda.DataTable;
//import life.genny.qwanda.GennyToken;
//import life.genny.qwanda.attribute.Attribute;
//import life.genny.qwanda.attribute.EntityAttribute;

//
//
@Path("/qwanda/entityattributes")
@RegisterForReflection
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityAttributeResource {

    private static final Logger log = Logger.getLogger(EntityAttributeResource.class);
//
//    @ConfigProperty(name = "default.realm", defaultValue = "genny")
//    String defaultRealm;
//
//
//    @Inject
//    SecurityIdentity securityIdentity;
//
    @Inject
    JsonWebToken accessToken;

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

	@POST
	@Consumes("application/json")
	@Transactional
	public Response create(@Context UriInfo uriInfo, @Valid EntityAttribute entity) {
		GennyToken userToken = new GennyToken(accessToken.getRawToken());
		if (entity == null) {
			throw new WebApplicationException("Error in EntityAttribute. ",
					Status.NO_CONTENT);			
		}

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. EntityaAttribute not being created",
					Status.FORBIDDEN);				
		}
		
		entity.id = null;
		entity.persist();
		log.info("Received EntityAttribute ! "+entity);

		URI uri = uriInfo.getAbsolutePathBuilder().path(EntityAttributeResource.class, "findById").build(entity.id);
		return Response.created(uri).build();	}
	
	
	@GET
	@Transactional
	public Response fetchAll() {
	    GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }
		final List<EntityAttribute> entitys = EntityAttribute.listAll();
		EntityAttribute[] atArr = new EntityAttribute[entitys.size()];
		atArr = entitys.toArray(atArr);
		QDataEntityAttributeMessage msg = new QDataEntityAttributeMessage(atArr);
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

        EntityAttribute ea = EntityAttribute.findById(id);
        if (ea == null) {
            throw new WebApplicationException("EntityAttribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        return Response.status(Status.OK).entity(ea).build();
    }



    @Path("/{id}")
    @PUT
    @Transactional
    public Response update(@PathParam("id") final Long id, @Valid EntityAttribute entity) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being created",
					Status.FORBIDDEN);				
		}

        EntityAttribute item = EntityAttribute.findById(id);
        if (item == null) {
            throw new WebApplicationException("EntityAttribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }

//        item.attribute = entity.attribute;
//        item.inferred = entity.inferred;
//        item.privacyFlag = entity.privacyFlag;
//        item.readonly = entity.readonly;
//        item.realm = entity.realm; 
        item.value = entity.value;
//        item.weight = entity.weight;
        
        item.persist();

 
        return Response.status(Status.OK).entity(item).build();
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public Response delete(@PathParam("id") final Long id) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

        if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being deleted",
					Status.FORBIDDEN);				
		}

        EntityAttribute item = EntityAttribute.findById(id);
        if (item == null) {
            throw new WebApplicationException("EntityAttribute with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        EntityAttribute.deleteById(item.id);
        
        return Response.status(Status.OK).build();
    }

    @GET
    @Path("/datatable")
    public DataTable<EntityAttribute> datatable(@QueryParam(value = "draw") int draw, @QueryParam(value = "start") int start,
                                                @QueryParam(value = "length") int length, @QueryParam(value = "search[value]") String searchVal

    ) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());

        life.genny.qwanda.DataTable<EntityAttribute> result = new DataTable<>();
        
        if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
 			throw new WebApplicationException("User not recognised. Entityies not being fetched",
 					Status.FORBIDDEN);				
 		}

            searchVal = "";
            result.setDraw(draw);

            PanacheQuery<EntityAttribute> filteredDevice;

            if (searchVal != null && !searchVal.isEmpty()) {
                filteredDevice = EntityAttribute.<EntityAttribute>find("content like :search",
                        Parameters.with("search", "%" + searchVal + "%"));
            } else {
                filteredDevice = EntityAttribute.findAll();
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
            result.setRecordsTotal(EntityAttribute.count());
   
        return result;

    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        log.info("EntityAttribute Endpoint starting");
 
    }

    @Transactional
    void onShutdown(@Observes ShutdownEvent ev) {
        log.info("EntityAttribute Endpoint Shutting down");
    }
}