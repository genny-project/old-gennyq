package life.genny.vertx.eventbus;

//import io.vertx.axle.core.eventbus.EventBus;
//import io.vertx.axle.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
//import life.genny.models.entity.BaseEntity;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/")
public class ChatResource {

  @Inject
  EventBus eventBus;

  @POST
  @Path("join")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public BaseEntityResp join(BaseEntityReq be ) {//@Valid User user
    BaseEntityReq baseEntity = new BaseEntityReq( );
    baseEntity.code = "PRI_GENNY";
    baseEntity.name = "test";
//    baseEntity.realm ="internmatch";
//    return eventBus.<JsonObject>send("join", JsonObject.mapFrom(baseEntity))
//      .thenApply(Message::body)
//      .thenApply(jsonObject -> jsonObject.mapTo(BaseEntity.class));

    return eventBus.<JsonObject>request("join",  JsonObject.mapFrom(baseEntity))
            .onItem()
            .transform(Message::body)
            .map(jsonObject -> jsonObject.mapTo(BaseEntityResp.class))
            .await()
            .atMost(Duration.ofSeconds(5));
  }

//  @PUT
//  @Path("leave")
//  @Consumes(MediaType.APPLICATION_JSON)
//  public CompletionStage<String> leave(@Email String username) {
//    return eventBus.<String>send("leave", username)
//      .thenApply(Message::body)
//      .exceptionally(Throwable::getMessage);
//  }
//
//  @GET
//  @Path("members")
//  @Produces(MediaType.APPLICATION_JSON)
//  public CompletionStage<JsonArray> members() {
//    return eventBus.<JsonArray>send("get.members", "")
//      .thenApply(Message::body);
//  }
//
//  @POST
//  @Produces(MediaType.TEXT_HTML)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("chat")
//  public CompletionStage<String> chat(@Valid ChatMessage message) {
//    return eventBus.<String>send("chat", JsonObject.mapFrom(message))
//      .thenApply(Message::body)
//      .exceptionally(Throwable::getMessage);
//  }
//
//  @GET
//  @Produces(MediaType.SERVER_SENT_EVENTS)
//  @Path("stream")
//  public Publisher<String> stream() {
//    return eventBus.<String>consumer("stream").toPublisherBuilder()
//      .map(Message::body)
//      .buildRs();
//  }
}