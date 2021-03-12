package life.genny.strategy.model;

import java.util.Map;
import io.vertx.core.eventbus.EventBus;
import life.genny.models.message.QMessageGennyMSG;


public interface QMessageProvider {
	
	public void sendMessage(QBaseMSGMessage message, EventBus eventBus, Map<String, Object> contextMap);
	public QBaseMSGMessage setGenericMessageValue(QMessageGennyMSG message, Map<String, Object> entityTemplateMap, String token);
	public QBaseMSGMessage setGenericMessageValueForDirectRecipient(QMessageGennyMSG message, Map<String, Object> entityTemplateMap, String token, String to);

}
