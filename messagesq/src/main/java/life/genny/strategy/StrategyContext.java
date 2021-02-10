package life.genny.strategy;

import life.genny.application.Configuration;
import life.genny.strategy.model.GennyMessage;
import life.genny.strategy.model.QBaseMSGMessageType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class StrategyContext {

   @Inject
   private Map<QBaseMSGMessageType, Strategy> strategies;


   public void execute(GennyMessage gennyMessage) throws Exception {

      if(!strategies.containsKey(gennyMessage.getQBaseMSGMessageType())){
         throw new Exception(String
                 .format("Strategy may not config in the class of %s", Configuration.class.getName()));
      }
      QBaseMSGMessageType qBaseMSGMessageType = gennyMessage.getQBaseMSGMessageType();
      strategies.get(qBaseMSGMessageType).send(gennyMessage);
   }
}