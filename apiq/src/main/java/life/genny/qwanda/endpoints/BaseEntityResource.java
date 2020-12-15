package life.genny.qwanda.endpoints;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.qwanda.DataTable;
import life.genny.qwanda.GennyToken;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;


@Path("/qwanda/baseentitys")
@RegisterForReflection
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BaseEntityResource {

    private static final Logger log = Logger.getLogger(BaseEntityResource.class);

    @Inject
    JsonWebToken accessToken;

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

	@POST
	@Transactional
	public Response create(@Context UriInfo uriInfo, @Valid BaseEntity entity) {
		GennyToken userToken = new GennyToken(accessToken.getRawToken());
		if (entity == null) {
			throw new WebApplicationException("Error in BaseEntity. ",
					Status.NO_CONTENT);			
		}

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")  && !userToken.hasRole("service")&& !userToken.hasRole("test")) {
			throw new WebApplicationException("User not recognised. EntityaAttribute not being created",
					Status.FORBIDDEN);				
		}
		
		entity.id = null;
		entity.persist();
		log.info("Received BaseEntity ! "+entity);

		URI uri = uriInfo.getAbsolutePathBuilder().path(BaseEntityResource.class, "findById").build(entity.id);
		return Response.created(uri).build();	}
	
	
//	@GET
//	@Transactional
//	public Response fetchAll() {
//	    GennyToken userToken = new GennyToken(accessToken.getRawToken());
//	    if (userToken == null) {
//	    	return Response.status(Status.FORBIDDEN).build();
//	    }
//		final List<EntityAttribute> entitys = EntityAttribute.listAll();
//		EntityAttribute[] atArr = new EntityAttribute[entitys.size()];
//		atArr = entitys.toArray(atArr);
//		QDataEntityAttributeMessage msg = new QDataEntityAttributeMessage(atArr);
//		return Response.status(Status.OK).entity(msg).build();
//
//	}

	@GET
	@Path("/{code}")
	@Transactional
	public Response fetch(@PathParam("code") final String code) {
	    GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }
	    
		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin") && !userToken.hasRole("service") && !userToken.hasRole("test")) {
			throw new WebApplicationException("User not recognised. BaseEntity not being fetched",
					Status.FORBIDDEN);				
		}


		BaseEntity be = BaseEntity.find("code", code).firstResult();
		return Response.status(200).entity(be).build();
	}
	
	
 

    @Path("/id/{id}")
    @GET
    public Response findById(@PathParam("id") final Long id) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin") && !userToken.hasRole("service")  && !userToken.hasRole("test")) {
			throw new WebApplicationException("User not recognised. BaseEntity not being fetched",
					Status.FORBIDDEN);				
		}

        BaseEntity ea = BaseEntity.findById(id);
        if (ea == null) {
            throw new WebApplicationException("BaseEntity with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        return Response.status(Status.OK).entity(ea).build();
    }



    @Path("/{id}")
    @PUT
    @Transactional
    public Response update(@PathParam("id") final Long id, @Valid BaseEntity entity) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());
	    if (userToken == null) {
	    	return Response.status(Status.FORBIDDEN).build();
	    }

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin") && !userToken.hasRole("service")  && !userToken.hasRole("test")) {
			throw new WebApplicationException("User not recognised. BaseEntity not being fetched",
					Status.FORBIDDEN);				
		}

        BaseEntity item = BaseEntity.findById(id);
        if (item == null) {
            throw new WebApplicationException("BaseEntity with id of " + id + " does not exist.", Status.NOT_FOUND);
        }

        item.name = entity.name;
        item.updated = LocalDateTime.now(ZoneId.of("UTC"));
        
        // Now look at the entityAttributes
        
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

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin") && !userToken.hasRole("service")  && !userToken.hasRole("test")) {
			throw new WebApplicationException("User not recognised. BaseEntity not being fetched",
					Status.FORBIDDEN);				
		}

        BaseEntity item = BaseEntity.findById(id);
        if (item == null) {
            throw new WebApplicationException("BaseEntity with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
 
        BaseEntity.deleteById(item.id);
        
        return Response.status(Status.OK).build();
    }

    @GET
    @Path("/datatable")
    public DataTable<BaseEntity> datatable(@QueryParam(value = "draw") int draw, @QueryParam(value = "start") int start,
                                     @QueryParam(value = "length") int length, @QueryParam(value = "search[value]") String searchVal

    ) {
        GennyToken userToken = new GennyToken(accessToken.getRawToken());

        life.genny.qwanda.DataTable<BaseEntity> result = new DataTable<>();
        
        if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
 			throw new WebApplicationException("User not recognised. Entities not being fetched",
 					Status.FORBIDDEN);				
 		}

            searchVal = "";
            result.setDraw(draw);

            PanacheQuery<BaseEntity> filteredDevice;

            if (searchVal != null && !searchVal.isEmpty()) {
                filteredDevice = BaseEntity.<BaseEntity>find("content like :search",
                        Parameters.with("search", "%" + searchVal + "%"));
            } else {
                filteredDevice = BaseEntity.findAll();
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
        log.info("BaseEntity Endpoint starting");
 
    }

    @Transactional
    void onShutdown(@Observes ShutdownEvent ev) {
        log.info("BaseEntity Endpoint Shutting down");
    }
}