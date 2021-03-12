package life.genny.models.message;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class QBaseMSGAttachment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum AttachmentType {
		INLINE,
		NON_INLINE
	}
	
	@Expose
	private AttachmentType attachmentType; 
	
	@Expose
	private String contentType;

	@Expose
	private String link;
	
	@Expose
	private Boolean isMergeRequired;
	
	@Expose
	private String namePrefix;
	/**
	 * @return the attachmentType
	 */
	public AttachmentType getAttachmentType() {
		return attachmentType;
	}
	/**
	 * @param attachmentType the attachmentType to set
	 */
	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}
	
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the isMergeRequired
	 */
	public Boolean getIsMergeRequired() {
		return isMergeRequired;
	}
	/**
	 * @param isMergeRequired the isMergeRequired to set
	 */
	public void setIsMergeRequired(Boolean isMergeRequired) {
		this.isMergeRequired = isMergeRequired;
	}
	/**
	 * @return the namePrefix
	 */
	public String getNamePrefix() {
		return namePrefix;
	}
	/**
	 * @param namePrefix the namePrefix to set
	 */
	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QBaseMSGAttachment [attachmentType=" + attachmentType + ", contentType=" + contentType + ", link="
				+ link + ", isMergeRequired=" + isMergeRequired + ", namePrefix=" + namePrefix + "]";
	}
	
	
	public QBaseMSGAttachment(AttachmentType attachmentType, String contentType, String link, Boolean isMergeRequired,
			String namePrefix) {
		super();
		this.attachmentType = attachmentType;
		this.contentType = contentType;
		this.link = link;
		this.isMergeRequired = isMergeRequired;
		this.namePrefix = namePrefix;
	}

}
