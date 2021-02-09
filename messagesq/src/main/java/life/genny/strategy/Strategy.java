package life.genny.strategy;

import life.genny.strategy.model.GennyMessage;

public abstract class Strategy {

    public abstract void send(GennyMessage gennyMessage) throws Exception;

}
