/*
 * (C) Copyright 2017 GADA Technology (http://www.outcome-hub.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors: Adam Crow Byron Aguirre
 */

package life.genny.qwanda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.adapters.LocalDateTimeAdapter;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.exception.BadDataException;

/**
 * Answer is the abstract base class for all answers managed in the Qwanda
 * library. An Answer object is used as a means of storing information from a
 * source about a target attribute. This answer information includes:
 * <ul>
 * <li>The Associated Ask
 * <li>The time at which the answer was created
 * <li>The status of the answer e.g Expired, Refused, Answered
 * </ul>
 * <p>
 * Answers represent the manner in which facts about a target from sources are
 * stored. Each Answer is associated with an attribute.
 * <p>
 *
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */



@Table(name = "answer",
        indexes = {
                //      @Index(columnList = "sourcecode", name =  "code_idx"), // Don't need to index sourcecode
                @Index(columnList = "targetcode", name = "code_idx"),
                @Index(columnList = "attributecode", name = "code_idx"),
                @Index(columnList = "realm", name = "code_idx")
        }//,

)
@RegisterForReflection
public class Answer extends PanacheEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

	private static final String REGEX_REALM = "[a-zA-Z0-9]+";
	private static final String DEFAULT_REALM = "genny";

	@NotEmpty
	@JsonbTransient
	@Pattern(regexp = REGEX_REALM, message = "Must be valid Realm Format!")
	public String realm=DEFAULT_REALM;

	
	@JsonbTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC"));


	
    /**
     * A field that stores the human readable value of the answer.
     * <p>
     */
    @NotNull
    @Type(type = "text")
    @Column(name = "value", updatable = true, nullable = false)
    public String value;

    /**
     * A field that stores the human readable attributecode associated with this
     * answer.
     * <p>
     */
    @NotNull
    @Size(max = 250)
    @Column(name = "attributecode", updatable = true, nullable = false)
    public String attributeCode;

//	@JsonbTypeAdapter(AttributeAdapter.class)
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ATTRIBUTE_ID", nullable = false)
	public Attribute attribute;

    /**
     * Store the askId (if present)
     */
    public Long askId;

    /**
     * A field that stores the human readable targetcode associated with this
     * answer.
     * <p>
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "targetcode", updatable = true, nullable = true)
 
    public String targetCode;

    /**
     * A field that stores the human readable sourcecode associated with this
     * answer.
     * <p>
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "sourcecode", updatable = true, nullable = true)
    public String sourceCode;

    /**
     * Store the Expired boolean value of the attribute for the baseEntity
     */
    public Boolean expired = false;

    /**
     * Store the Refused boolean value of the attribute for the baseEntity
     */
    public Boolean refused = false;

    /**
     * Store the relative importance of the attribute for the baseEntity
     */

    public Double weight = 0.0;

    /**
     * Store whether this answer was inferred
     */

    public Boolean inferred = false;


    public Boolean changeEvent = false;

    @Transient
    // Provide a clue to any new attribute type that may be needed if the attribute does not exist yet, e.g. java.util.Double
    public String dataType = null;

 
    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    public Answer() {
        // dummy for hibernate
    }

    /**
     * Constructor.
     *
     * @param source    The source associated with this Answer
     * @param target    The target associated with this Answer
     * @param attribute The attribute associated with this Answer
     * @param value     The associated String value
     */
    public Answer(final BaseEntity source, final BaseEntity target, final Attribute attribute, final String value) {
        this.sourceCode = source.code;
        this.targetCode = target.code;
        this.attributeCode = attribute.code;
        this.attribute = attribute;
        this.setValue(value);
        checkInputs();
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated String value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final String value) {
        this.sourceCode = sourceCode;
        this.targetCode = targetCode;
        this.attributeCode = attributeCode;
        this.setValue(value);
        checkInputs();
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated Double value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final Double value) {
        this(sourceCode, targetCode, attributeCode, value + "");
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated String value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final Double value, final Boolean changeEvent, final Boolean inferred) {
        this(sourceCode, targetCode, attributeCode, value + "");
        this.changeEvent = changeEvent;
        this.inferred = inferred;
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated Long value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final Long value) {
        this(sourceCode, targetCode, attributeCode, value + "");
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated LocalDateTime value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final LocalDateTime value) {
        this(sourceCode, targetCode, attributeCode, value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated LocalDate value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final LocalDate value) {
        this(sourceCode, targetCode, attributeCode, value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated LocalTime value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final LocalTime value) {
        this(sourceCode, targetCode, attributeCode, value.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated  Integer value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final Integer value) {
        this(sourceCode, targetCode, attributeCode, value + "");
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated  Boolean value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final Boolean value) {
        this(sourceCode, targetCode, attributeCode, value ? "TRUE" : "FALSE");

    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated String value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final String value, final Boolean changeEvent, final Boolean inferred) {
        this.sourceCode = sourceCode;
        this.targetCode = targetCode;
        this.attributeCode = attributeCode;
        this.setValue(value);
        checkInputs();
        this.changeEvent = changeEvent;
        this.inferred = inferred;
    }

    /**
     * Constructor.
     *
     * @param sourceCode    The sourceCode associated with this Answer
     * @param targetCode    The targetCode associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated String value
     */
    public Answer(final String sourceCode, final String targetCode, final String attributeCode, final String value, final Boolean changeEvent) {
        this(sourceCode, targetCode, attributeCode, value, changeEvent, false);
    }

    /**
     * Constructor.
     *
     * @param source        The source BE associated with this Answer
     * @param target        The target BE associated with this Answer
     * @param attributeCode The attributeCode associated with this Answer
     * @param value         The associated String value
     */
    public Answer(final BaseEntity source, final BaseEntity target, final String attributeCode, final String value) {
        this.sourceCode = source.code;
        this.targetCode = target.code;
        this.attributeCode = attributeCode;
        this.setValue(value);
        checkInputs();
    }

    /**
     * Constructor.
     *
     * @param aAsk  The ask that created this answer
     * @param value The associated String value
     * @throws BadDataException
     */
    public Answer(final Ask aAsk, final String value) throws BadDataException {
        this.askId = aAsk.id;
        this.attributeCode = aAsk.getQuestion().getAttribute().code;
        this.attribute = aAsk.getQuestion().getAttribute();
        this.sourceCode = aAsk.getSourceCode();
        this.targetCode = aAsk.getTargetCode();
        this.setValue(value);
        checkInputs();
        // this.ask.add(this);
    }

    /**
     * Constructor.
     *
     * @param aAsk    The ask that created this answer
     * @param expired did this ask expire?
     * @param refused did the user refuse this question?
     * @throws BadDataException
     */
    public Answer(final Ask aAsk, final Boolean expired, final Boolean refused) throws BadDataException {
        // this.ask = aAsk;
        // this.attributeCode = this.ask.getQuestion().getAttribute().getCode();
        // this.attribute = this.ask.getQuestion().getAttribute();
        // this.sourceCode = this.ask.getSource().getCode();
        // this.targetCode = this.ask.getTarget().getCode();

        this.setRefused(refused);
        this.setExpired(expired);
        checkInputs();
        // this.ask.add(this);
    }



    @Transient
    @JsonbTransient
    public Date getCreatedDate() {
        final Date out = Date.from(created.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }



    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final String value) {
        if (value != null) {
            this.value = value.trim();
        } else {
            this.value = "";
        }
    }

    /**
     * @return the expired
     */
    public Boolean getExpired() {
        return expired;
    }

    /**
     * @param expired the expired to set
     */
    public void setExpired(final Boolean expired) {
        this.expired = expired;
    }

    /**
     * @return the refused
     */
    public Boolean getRefused() {
        return refused;
    }

    /**
     * @param refused the refused to set
     */
    public void setRefused(final Boolean refused) {
        this.refused = refused;
    }

    /**
     * @return the weight
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(final Double weight) {
        this.weight = weight;
    }

    // /**
    // * @return the ask
    // */
    // public Ask getAsk() {
    // return ask;
    // }
    //
    // /**
    // * @param ask the ask to set
    // */
    // public void setAsk(final Ask ask) {
    // this.ask = ask;
    // }

    /**
     * @return the attributeCode
     */
    public String getAttributeCode() {
        return attributeCode;
    }

    /**
     * @param attributeCode the attributeCode to set
     */
    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    /**
     * @return the askId
     */
    public Long getAskId() {
        return askId;
    }

    /**
     * @param askId the askId to set
     */
    public void setAskId(final Long askId) {
        this.askId = askId;
    }

    /**
     * @return the inferred
     */
    public Boolean getInferred() {
        return inferred;
    }

    /**
     * @param inferred the inferred to set
     */
    public void setInferred(Boolean inferred) {
        this.inferred = inferred;
    }

    /**
     * @return the targetCode
     */
    public String getTargetCode() {
        return targetCode;
    }

    /**
     * @param targetCode the targetCode to set
     */
    public void setTargetCode(final String targetCode) {
        this.targetCode = targetCode;
    }

    /**
     * @return the sourceCode
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * @param sourceCode the sourceCode to set
     */
    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(final Attribute attribute) {
        this.attribute = attribute;
        if (this.dataType == null) {
            setDataType(attribute.dataType.getClassName());
        }
    }

    /**
     * @return the changeEvent
     */
    public Boolean getChangeEvent() {
        return changeEvent;
    }

    /**
     * @param changeEvent the changeEvent to set
     */
    public void setChangeEvent(Boolean changeEvent) {
        this.changeEvent = changeEvent;
    }


    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }


    public String getUniqueCode() {
        return getSourceCode() + ":" + getTargetCode() + ":" + getAttributeCode();
    }



    @Override
	public String toString() {
		return "Answer [" + (realm != null ? "realm=" + realm + ", " : "")
				+ (value != null ? "value=" + value + ", " : "")
				+ (attributeCode != null ? "attributeCode=" + attributeCode + ", " : "")
				+ (targetCode != null ? "targetCode=" + targetCode + ", " : "")
				+ (sourceCode != null ? "sourceCode=" + sourceCode + ", " : "")
				+ (inferred != null ? "inferred=" + inferred + ", " : "")
				+ (changeEvent != null ? "changeEvent=" + changeEvent : "") + "]";
	}

    
    
	@Override
	public int hashCode() {
		return Objects.hash(askId, attributeCode, realm, sourceCode, targetCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Answer))
			return false;
		Answer other = (Answer) obj;
		return Objects.equals(askId, other.askId) && Objects.equals(attributeCode, other.attributeCode)
				&& Objects.equals(realm, other.realm) && Objects.equals(sourceCode, other.sourceCode)
				&& Objects.equals(targetCode, other.targetCode);
	}

	private void checkInputs() {
        if (this.sourceCode == null) throw new NullPointerException("SourceCode cannot be null");
        if (this.targetCode == null) throw new NullPointerException("targetCode cannot be null");
        if (this.attributeCode == null) throw new NullPointerException("attributeCode cannot be null");
    }

}
