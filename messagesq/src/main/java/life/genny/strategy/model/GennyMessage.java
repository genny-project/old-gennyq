package life.genny.strategy.model;

import java.util.Map;

public class GennyMessage {

    private String body;

    private String recipient;

    private QBaseMSGMessageType QBaseMSGMessageType;

    private String templateId;

    private Map<String,String> templateData;

    public Map<String, String> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, String> templateData) {
        this.templateData = templateData;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public QBaseMSGMessageType getQBaseMSGMessageType() {
        return QBaseMSGMessageType;
    }

    public void setQBaseMSGMessageType(QBaseMSGMessageType QBaseMSGMessageType) {
        this.QBaseMSGMessageType = QBaseMSGMessageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
