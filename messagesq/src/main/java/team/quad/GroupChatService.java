package team.quad;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import life.genny.qwanda.entity.BaseEntity;

public class GroupChatService extends AbstractVerticle {

    private Map<String, BaseEntity> members;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.members = new HashMap<>();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Join group
        vertx.eventBus().<JsonObject>consumer("join", message -> {
                BaseEntity baseEntity = message.body().mapTo(BaseEntity.class);
                System.out.println("ok!");
                members.put(baseEntity.code, baseEntity);
                message.reply(JsonObject.mapFrom(baseEntity));
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
