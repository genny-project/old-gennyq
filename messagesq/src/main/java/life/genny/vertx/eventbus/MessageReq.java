package life.genny.vertx.eventbus;



import java.io.Serializable;


public class MessageReq implements Serializable {

     String baseentitycode;

      public MessageReq(){

      }

    public String getBaseentitycode() {
        return baseentitycode;
    }

    public void setBaseentitycode(String baseentitycode) {
        this.baseentitycode = baseentitycode;
    }
}