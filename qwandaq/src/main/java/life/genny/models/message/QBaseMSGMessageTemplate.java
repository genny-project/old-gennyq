package life.genny.qwanda.message;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import life.genny.qwanda.CoreEntity;


@SuppressWarnings("deprecation")
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
@Table(name = "template", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "realm"}))
@Entity
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class QBaseMSGMessageTemplate extends CoreEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    static public final String REGEX_CODE = "[A-Z]{3}\\_[A-Z0-9\\.\\-\\@\\_]*";

    /**
     * A field that stores the unique code name of the attribute.
     * <p>
     * p Note that the prefix of the attribute can specify the source. e.g.
     * FBK_BIRTHDATE indicates that the attribute represents the facebook value
     */
    @NotNull
    @Size(max = 64)
    @Pattern(regexp = REGEX_CODE, message = "Must be valid Code!")
    @Column(name = "code", updatable = false, nullable = false)
    @Expose
    private String code;


    /**
     * A field that stores the description.
     * <p>
     */
    @NotNull
    @Column(name = "description", updatable = true, nullable = false)
    @Expose
    private String description;


    /**
     * A field that stores the message subject.
     * <p>
     */
    @NotNull
    @Column(name = "subject", updatable = true, nullable = false)
    @Expose
    private String subject;


    /**
     * A field that stores the email template doc id.
     * <p>
     */
    @NotNull
    @Column(name = "email", updatable = true, nullable = false)
    @Expose
    private String email_templateId;


    /**
     * A field that stores the email template doc id.
     * <p>
     */
    @NotNull
    @Column(name = "sms", updatable = true, nullable = false, length = 1024)
    @Expose
    private String sms_template;


    /**
     * A field that stores the toast template doc id.
     */
    @NotNull
    @Column(name = "toast", updatable = true, nullable = false, length = 1024)
    @Expose
    private String toast_template;


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getEmail_templateId() {
        return email_templateId;
    }


    public void setEmail_templateId(String email_templateId) {
        this.email_templateId = email_templateId;
    }


    public String getSms_template() {
        return sms_template;
    }


    public void setSms_template(String sms_template) {
        this.sms_template = sms_template;
    }


    /**
     * @return the toast_template
     */
    public String getToast_template() {
        return toast_template;
    }


    /**
     * @param toast_template the toast_template to set
     */
    public void setToast_template(String toast_template) {
        this.toast_template = toast_template;
    }

    public String getCode() {
        return code;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QBaseMSGMessageTemplate [" + (description != null ? "description=" + description + ", " : "")
                + (subject != null ? "subject=" + subject + ", " : "")
                + (email_templateId != null ? "email_templateId=" + email_templateId + ", " : "")
                + (sms_template != null ? "sms_template=" + sms_template + ", " : "")
                + (toast_template != null ? "toast_template=" + toast_template + ", " : "")
                + (getCode() != null ? "getCode()=" + getCode() + ", " : "")
                + (super.toString() != null ? "toString()=" + super.toString() + ", " : "") + "hashCode()=" + hashCode()
                + ", " + (getId() != null ? "getId()=" + getId() + ", " : "")
                + (getName() != null ? "getName()=" + getName() + ", " : "")
                + (getCreated() != null ? "getCreated()=" + getCreated() + ", " : "")
                + (getUpdated() != null ? "getUpdated()=" + getUpdated() + ", " : "")
                + (getRealm() != null ? "getRealm()=" + getRealm() + ", " : "")
                + (getCreatedDate() != null ? "getCreatedDate()=" + getCreatedDate() + ", " : "")
                + (getUpdatedDate() != null ? "getUpdatedDate()=" + getUpdatedDate() + ", " : "")
                + (getClass() != null ? "getClass()=" + getClass() : "") + "]";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
