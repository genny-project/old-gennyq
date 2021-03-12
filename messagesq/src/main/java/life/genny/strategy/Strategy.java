package life.genny.strategy;

import life.genny.models.message.QMessageGennyMSG;
import life.genny.strategy.model.GennyMessage;

import java.util.Map;

public abstract class Strategy {

    public abstract void send(GennyMessage gennyMessage) throws Exception;

    public abstract void send(QMessageGennyMSG qMessageGennyMSG) throws Exception;

}
