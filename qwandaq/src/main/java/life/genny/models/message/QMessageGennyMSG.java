package life.genny.models.message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class QMessageGennyMSG extends QMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE_TYPE = "MSG_MESSAGE";
	
	@Expose
	private String template_code;
	
	@Expose
	private QBaseMSGMessageType msgMessageType;

	@Expose
	private QBaseMSGMessageType[] msgMessageTypes;
	
	@Expose
	private String[] recipientArr;
	
	@Expose
	private Map<String, String> messageContextMap;
	
	@Expose
	private List<QBaseMSGAttachment> attachmentList;
	
	@Expose
	private String[] to;
	/**
	 * @return the template_code
	 */
	public String getTemplate_code() {
		return template_code;
	}

	/**
	 * @param template_code the template_code to set
	 */
	public void setTemplate_code(String template_code) {
		this.template_code = template_code;
	}

	/**
	 * @return the msgMessageType
	 */
	public QBaseMSGMessageType getMsgMessageType() {
		return msgMessageType;
	}

	/**
	 * @param msgMessageType the msgMessageType to set
	 */
	public void setMsgMessageType(QBaseMSGMessageType msgMessageType) {
		this.msgMessageType = msgMessageType;
	}

	/**
	 * @return the recipientArr
	 */
	public String[] getRecipientArr() {
		return recipientArr;
	}

	public QBaseMSGMessageType[] getMsgMessageTypes() {
		return msgMessageTypes;
	}

	/**
	 * @param recipientArr the recipientArr to set
	 */
	public void setRecipientArr(String[] recipientArr) {
		this.recipientArr = recipientArr;
	}

	/**
	 * @return the messageContextMap
	 */
	public Map<String, String> getMessageContextMap() {
		return messageContextMap;
	}

	/**
	 * @param messageContextMap the messageContextMap to set
	 */
	public void setMessageContextMap(Map<String, String> messageContextMap) {
		this.messageContextMap = messageContextMap;
	}
	
	/**
	 * @return the attachmentList
	 */
	public List<QBaseMSGAttachment> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<QBaseMSGAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the to
	 */
	public String[] getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String[] to) {
		this.to = to;
	}

	public QMessageGennyMSG(String msg_type, QBaseMSGMessageType messageType, String templateCode, Map<String, String> contextMap, String[] recipientArr, QBaseMSGMessageType[] messageTypes) {
		this( msg_type, messageType, templateCode, contextMap,recipientArr);
		this.msgMessageTypes = messageTypes;
	}

	public QMessageGennyMSG(String msg_type, QBaseMSGMessageType messageType, String templateCode, Map<String, String> contextMap, String[] recipientArr) {
		super(msg_type);
		this.template_code = templateCode;
		this.msgMessageType = messageType;
		this.messageContextMap = contextMap;
		this.recipientArr = recipientArr;
	}
	

	
	
	public QMessageGennyMSG(String msg_type, QBaseMSGMessageType messageType, String templateCode, Map<String, String> contextMap, String[] recipientArr, List<QBaseMSGAttachment> attachmentList) {
		super(msg_type);
		this.template_code = templateCode;
		this.msgMessageType = messageType;
		this.messageContextMap = contextMap;
		this.recipientArr = recipientArr;
		this.attachmentList = attachmentList;
	}
	
	

	public QMessageGennyMSG(String msg_type, String template_code, QBaseMSGMessageType msgMessageType,
			Map<String, String> messageContextMap, List<QBaseMSGAttachment> attachmentList, String[] to) {
		super(msg_type);
		this.template_code = template_code;
		this.msgMessageType = msgMessageType;
		this.messageContextMap = messageContextMap;
		this.attachmentList = attachmentList;
		this.to = to;
	}
	
	

	public QMessageGennyMSG(String msg_type, String template_code, QBaseMSGMessageType msgMessageType,
			Map<String, String> messageContextMap, String[] to) {
		super(msg_type);
		this.template_code = template_code;
		this.msgMessageType = msgMessageType;
		this.messageContextMap = messageContextMap;
		this.to = to;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QMessageGennyMSG [template_code=" + template_code + ", msgMessageType=" + msgMessageType
				+ ", recipientArr=" + Arrays.toString(recipientArr) + ", messageContextMap=" + messageContextMap
				+ ", attachmentList=" + attachmentList + ", to=" + Arrays.toString(to) + "]";
	}

}
