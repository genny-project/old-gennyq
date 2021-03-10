package life.genny.strategy.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import life.genny.models.message.QBaseMSGAttachment;
import life.genny.models.message.QBaseMSGMessageType;

public class QBaseMSGMessage {
	
	public QBaseMSGMessage(String source, String target, String priority, String subject, String msgMessageData,
			List<QBaseMSGAttachment> attachmentList) {
		super();
		this.source = source;
		this.target = target;
		this.priority = priority;
		this.subject = subject;
		this.msgMessageData = msgMessageData;
		this.attachmentList = attachmentList;
	}
	public QBaseMSGMessage() {
		super();
	}


	@Expose
	private String source;
	@Expose
	private String target;
	@Expose
	private String priority;
	@Expose
	private String subject;
	@Expose
	private String msgMessageData;
	/*@Expose
	private String[] attachments;*/
	
	@Expose
	private List<QBaseMSGAttachment> attachmentList; 
	
	private String token;
	
	private QBaseMSGMessageType msgMessageType;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public QBaseMSGMessageType getMsgMessageType() {
		return msgMessageType;
	}
	public void setMsgMessageType(QBaseMSGMessageType msgMessageType) {
		this.msgMessageType = msgMessageType;
	}
	
	public String getMsgMessageData() {
		return msgMessageData;
	}
	public void setMsgMessageData(String msgMessageData) {
		this.msgMessageData = msgMessageData;
	}
	
	/*public String[] getAttachments() {
		return attachments;
	}
	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}*/
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QBaseMSGMessage [source=" + source + ", target=" + target + ", priority=" + priority + ", subject="
				+ subject + ", msgMessageData=" + msgMessageData + ", attachmentList=" + attachmentList + ", token="
				+ token + ", msgMessageType=" + msgMessageType + "]";
	}
	
}
