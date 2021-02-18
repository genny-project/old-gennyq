package life.genny.models.attribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.Value;
import life.genny.models.entity.BaseEntity;
import life.genny.utils.LocalDateTimeAdapter;

@Entity

@Table(name = "qbaseentity_attribute",
indexes = {
//		@Index(name = "ba_idx", columnList = "baseentitycode"),
//		@Index(name = "bb_idx", columnList = "attributeCode"),
		@Index(name = "bc_idx", columnList = "valueString"),
				@Index(name = "bd_idx", columnList = "valueBoolean"),
//				@Index(name = "bae_idx", columnList = "realm, ATTRIBUTE_ID,BASEENTITY_ID", unique = true)
    }
)

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@RegisterForReflection
public class EntityAttribute extends PanacheEntity {
//
	private static final Logger log = Logger.getLogger(EntityAttribute.class);

	private static final String REGEX_REALM = "[a-zA-Z0-9]+";
	private static final String DEFAULT_REALM = "genny";

	@NotEmpty
	@JsonbTransient
	@Pattern(regexp = REGEX_REALM, message = "Must be valid Realm Format!")
	public String realm = DEFAULT_REALM;

	@JsonbTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC"));

	@JsonbTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime updated;
//
//	@JsonbTypeAdapter(AttributeAdapter.class)
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attribute",nullable = false)
	public Attribute attribute;

	@JsonbTransient
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "baseentity",nullable = false)
	public BaseEntity baseentity;

//	// For compatibility initially
//	public String baseEntityCode;
	public String attributeCode;
	public String attributeName;

	@Embedded
	@NotNull
	public Value value = new Value();

	public Boolean readonly = false;

	@Transient
	public Integer index = 0; // used to assist with ordering

	/**
	 * Store the relative importance of the attribute for the baseEntity
	 */
	public Boolean inferred = false;

	/**
	 * Store the privacy of this attribute , i.e. Don't display
	 */
	public Boolean privacyFlag = false;

	public EntityAttribute() {
	}

	/**
	 * Constructor.
	 * 
	 * @param BaseEntity the entity that needs to contain attributes
	 * @param Attribute  the associated Attribute
	 * @param Weight     the weighted importance of this attribute (relative to the
	 *                   other attributes)
	 */
	public EntityAttribute(final BaseEntity baseEntity, final Attribute attribute, Double weight) {
		this(baseEntity, attribute, weight, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param BaseEntity the entity that needs to contain attributes
	 * @param Attribute  the associated Attribute
	 * @param Weight     the weighted importance of this attribute (relative to the
	 *                   other attributes)
	 * @param Value      the value associated with this attribute
	 */
	public EntityAttribute(final BaseEntity baseEntity, final Attribute attribute, Double weight, final Object value) {
		autocreateCreated();
		this.baseentity = baseEntity;
		this.attribute = attribute;
		setWeight(weight);
		privacyFlag = attribute.defaultPrivacyFlag;
		setValue(value);
	}

	@PreUpdate
	public void autocreateUpdate() {
		updated = LocalDateTime.now(ZoneId.of("UTC"));
	}

	@PrePersist
	public void autocreateCreated() {
		if (created == null)
			created = LocalDateTime.now(ZoneId.of("UTC"));
	}

	@Transient
	@JsonbTransient
	public Date getCreatedDate() {
		final Date out = Date.from(created.atZone(ZoneId.systemDefault()).toInstant());
		return out;
	}

	@Transient
	@JsonbTransient
	public Date getUpdatedDate() {
		if (updated == null)
			return null;
		final Date out = Date.from(updated.atZone(ZoneId.systemDefault()).toInstant());
		return out;
	}

	@SuppressWarnings("unchecked")
	@JsonbTransient
	@Transient
	public <T> T getValue() {
		return value.getValue();

	}

	@JsonbTransient
	@Transient
	public <T> void setValue(final Object value) {
		if (this.readonly) {
			log.error("Trying to set the value of a readonly EntityAttribute! " + attribute.code);
			return;
		}

		setValue(value, true);
	}

	@JsonbTransient
	@Transient
	public <T> void setValue(final Object value, final Boolean lock) {
		if (this.readonly) {
			log.error("Trying to set the value of a readonly EntityAttribute! " + attribute.code);
			return;
		}

		if (value == null) {
			this.value.setValue(this.attribute.defaultValue);
		} else {
			this.value.setValue(value);
		}
		// if the lock is set then 'Lock it in Eddie!'.
		if (lock) {
			this.readonly = true;
		}

	}

	@JsonbTransient
	@Transient
	public <T> void setLoopValue(final Object value) {
		setValue(value, false);
	}

	@JsonbTransient
	@Transient
	public String getAsString() {

		return value.toString();
	}

	@JsonbTransient
	@Transient
	public String getAsLoopString() {
		return value.toString();
	}

	@SuppressWarnings("unchecked")
	@JsonbTransient
	@Transient
	public <T> T getLoopValue() {
		return getValue();

	}

	public int compareTo(EntityAttribute obj) {
		if (this == obj)
			return 0;

		return value.compareTo(obj.value);
	}

	@Override
  public String toString() {
    return "EntityAttribute [attribute=" + attribute + ", attributeCode=" + attributeCode + ", attributeName="
        + attributeName  + ", created="
        + created + ", index=" + index + ", inferred=" + inferred + ", privacyFlag=" + privacyFlag
        + ", readonly=" + readonly + ", realm=" + realm + ", updated=" + updated + ", value=" + value + "]";
  }

	@SuppressWarnings("unchecked")
	@JsonbTransient
	@Transient
	public <T> T getObject() {

		return getValue();
	}

	@JsonbTransient
	@Transient
	public String getObjectAsString() {

		return value.toString();

	}

	@JsonbTransient
	@Transient
	public Boolean getValueBoolean() {
		return value.valueBoolean;
	}

	@JsonbTransient
	@Transient
	public String getValueString() {
		return value.valueString;
	}

	@JsonbTransient
	@Transient
	public Double getValueDouble() {
		return value.valueDouble;
	}

	@JsonbTransient
	@Transient
	public Integer getValueInteger() {
		return value.valueInteger;
	}

	@JsonbTransient
	@Transient
	public Long getValueLong() {
		return value.valueLong;
	}

	@JsonbTransient
	@Transient
	public LocalDateTime getValueDateTime() {
		return value.valueDateTime;
	}
	
	@JsonbTransient
	@Transient
	public LocalDate getValueDate() {
		return value.valueDate;
	}
	
	@JsonbTransient
	@Transient
	public LocalTime getValueTime() {
		return value.valueTime;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	@Transient
	public Double getWeight() {
		return value.weight;
	}

	@Transient
	public void setWeight(Double weight) {
		if (weight == null) {
			weight = 0.0; // This permits ease of adding attributes and hides
							// attribute from scoring.
		}
		value.weight = weight;
	}


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
    result = prime * result + ((inferred == null) ? 0 : inferred.hashCode());
    result = prime * result + ((privacyFlag == null) ? 0 : privacyFlag.hashCode());
    result = prime * result + ((readonly == null) ? 0 : readonly.hashCode());
    result = prime * result + ((realm == null) ? 0 : realm.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EntityAttribute other = (EntityAttribute) obj;
    if (attribute == null) {
      if (other.attribute != null)
        return false;
    } else if (!attribute.equals(other.attribute))
      return false;
    if (attributeName == null) {
      if (other.attributeName != null)
        return false;
    } else if (!attributeName.equals(other.attributeName))
      return false;
    if (baseentity == null) {
      if (other.baseentity != null)
        return false;
    } else if (!baseentity.equals(other.baseentity))
      return false;
 
    if (inferred == null) {
      if (other.inferred != null)
        return false;
    } else if (!inferred.equals(other.inferred))
      return false;
    if (privacyFlag == null) {
      if (other.privacyFlag != null)
        return false;
    } else if (!privacyFlag.equals(other.privacyFlag))
      return false;
    if (readonly == null) {
      if (other.readonly != null)
        return false;
    } else if (!readonly.equals(other.readonly))
      return false;
    if (realm == null) {
      if (other.realm != null)
        return false;
    } else if (!realm.equals(other.realm))
      return false;
    return true;
  }
}
