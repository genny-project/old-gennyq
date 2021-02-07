package life.genny.qwanda.message;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
@Table(name = "template", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "realm"}))
@Entity
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class QBaseMSGMessageTemplate extends PanacheEntity {

    /**
     *
     */
    public static final String REGEX_CODE = "[A-Z]{3}\\_[A-Z0-9\\.\\-\\@\\_]*";
    public static final String REGEX_NAME = "[\\pL0-9/\\:\\ \\_\\.\\,\\?\\>\\<\\%\\$\\&\\!\\*" + ""
            + "\\[\\]\\'\\-\\@\\(\\)]+.?";
    private static final String REGEX_REALM = "[a-zA-Z0-9]+";
    private static final String DEFAULT_REALM = "genny";


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
    public String code;

    @NotNull
    @Size(max = 128)
    @Pattern(regexp = REGEX_NAME, message = "Must contain valid characters for name")
    @Column(name = "name")
    public String name;

    @NotEmpty
    @JsonbTransient
    @Pattern(regexp = REGEX_REALM, message = "Must be valid Realm Format!")
    public String realm = DEFAULT_REALM;

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


    @JsonbTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC"));

    @JsonbTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime updated;

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


    @Transient
    @JsonIgnore
    public Date getCreatedDate() {
        final Date out = Date.from(created.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    @Transient
    @JsonIgnore
    public Date getUpdatedDate() {
        if (updated != null) {
            final Date out = Date.from(updated.atZone(ZoneId.systemDefault()).toInstant());
            return out;
        } else
            return null;
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
                + ", " + (this.id != null ? "getId()=" + this.id + ", " : "")
                + (this.name != null ? "getName()=" + this.name + ", " : "")
                + (this.created != null ? "getCreated()=" + this.created + ", " : "")
                + (this.updated != null ? "getUpdated()=" + this.updated + ", " : "")
                + (this.realm != null ? "getRealm()=" + realm + ", " : "")
                + (getCreatedDate() != null ? "getCreatedDate()=" + getCreatedDate() + ", " : "")
                + (getUpdatedDate() != null ? "getUpdatedDate()=" + getUpdatedDate() + ", " : "")
                + (getClass() != null ? "getClass()=" + getClass() : "") + "]";
    }

    public int compareTo(Object o) {
        return 0;
    }
}
