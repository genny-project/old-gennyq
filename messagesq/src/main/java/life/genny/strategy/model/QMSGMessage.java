package life.genny.strategy.model;

import java.util.Arrays;

import com.google.gson.annotations.Expose;
import life.genny.models.message.QBaseMSGMessageType;
import life.genny.models.message.QMessage;

public class QMSGMessage extends QMessage {
	
	private static final String MESSAGE_TYPE = "MSG_MESSAGE";
		
	@Expose
	private String template_code;
	@Expose
	private String[] msgMessageData;
	@Expose
	private QBaseMSGMessageType msgMessageType;
	@Expose
	private String code;
	@Expose
	private String[] attachments;
	
	//private List<QBaseMSGMessage> msgMessageData;
	
	
	/*public List<QBaseMSGMessage> getMsgMessageData() {
		return msgMessageData;
	}

	public void setMsgMessageData(List<QBaseMSGMessage> msgMessageData) {
		this.msgMessageData = msgMessageData;
	}*/

	public String getTemplate_code() {
		return template_code;
	}


	public void setTemplate_code(String template_code) {
		this.template_code = template_code;
	}

	public String[] getMsgMessageData() {
		return msgMessageData;
	}

	public void setMsgMessageData(String[] msgMessageData) {
		this.msgMessageData = msgMessageData;
	}

	public QBaseMSGMessageType getMsgMessageType() {
		return msgMessageType;
	}

	public void setMsgMessageType(QBaseMSGMessageType msgMessageType) {
		this.msgMessageType = msgMessageType;
	}
	
	public String[] getAttachments() {
		return attachments;
	}

	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}
	

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	@Override
	public String toString() {
		return "QMSGMessage [template_code=" + template_code + ", msgMessageData=" + Arrays.toString(msgMessageData)
				+ ", msgMessageType=" + msgMessageType + "]";
	}


	public QMSGMessage(String msg_type, String template_code, String[] msgMessageData,
			QBaseMSGMessageType msgMessageType, String[] attachments) {
		super(msg_type);
		this.template_code = template_code;
		this.msgMessageData = msgMessageData;
		this.msgMessageType = msgMessageType;
		this.attachments = attachments;
	}

	//For Test Message
	public QMSGMessage(String msg_type, QBaseMSGMessageType msgMessageType, String code,
			String[] attachments) {
		super(msg_type);
		this.msgMessageType = msgMessageType;
		this.code = code;
		this.attachments = attachments;
	}
	
	
	
}
