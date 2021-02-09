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
                MessageReq baseEntityreq = message.body().mapTo(MessageReq.class);
                System.out.println("ok!");
                BaseEntityResp resp = new BaseEntityResp();
                resp.setCode("PRI_GENNY");
                resp.setName("test");
                members.put(baseEntityreq.code, resp);
                message.reply(JsonObject.mapFrom(resp));



        });


        /*
        base entity code ===>>>>

        server down/slow -> dev op team send alert to all of them -> send sms & email

        message template (MSG_SERVER_ISSUE) WHERE? ===>>>> db fetch

        chris creates a json message structure, adding recipents, templates how we sent

        he wants to send by sms and email, etc

        he provides base entity message
        he fetches base entity for that server(anme url ip address, ) i get context

        write message to  message servers e.g kafka vertx

        sendgrid

        junit test

         */

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
