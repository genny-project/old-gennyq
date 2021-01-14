package life.genny.vertx.eventbus;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
//import life.genny.models.entity.BaseEntity;

import java.util.HashMap;
import java.util.Map;

public class GroupChatService extends AbstractVerticle {

    private Map<String, BaseEntityResp> members;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.members = new HashMap<>();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Join group
        vertx.eventBus().<JsonObject>consumer("join", message -> {
                BaseEntityReq baseEntityreq = message.body().mapTo(BaseEntityReq.class);
                System.out.println("ok!");
                BaseEntityResp resp = new BaseEntityResp();
                resp.setCode("PRI_GENNY");
                resp.setName("test");
                members.put(baseEntityreq.code, resp);
                message.reply(JsonObject.mapFrom(resp));
        });

//        // Leave group
//        vertx.eventBus().<String>consumer("leave", message -> {
//            members.remove(message.body());
//            message.reply("OK");
//        });
//
//        // Handle incoming chat message
//        vertx.eventBus().<JsonObject>consumer("chat", message -> {
//            ChatMessage chatMessage = message.body().mapTo(ChatMessage.class);
//            User member = members.get(chatMessage.getUsername());
//            if(Objects.isNull(member)) {
//                message.fail(1, "Not a member");
//            } else {
//                vertx.eventBus().publish("stream", member.getAlias() + ":" + chatMessage.getMessage());
//                message.reply("OK");
//            }
//        });
//
//        // Get members
//        vertx.eventBus().<JsonArray>consumer("get.members", message -> {
//            List<String> users = this.members.values().stream().map(User::toString).collect(Collectors.toList());
//            message.reply(new JsonArray(users));
//        });

        super.start(startFuture);
    }
}
