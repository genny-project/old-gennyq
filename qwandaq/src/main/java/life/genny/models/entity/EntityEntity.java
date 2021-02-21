package life.genny.models.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.Expose;
import life.genny.qwanda.entity.EntityEntityId;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.Link;
import life.genny.models.Value;
import life.genny.models.attribute.Attribute;
import life.genny.utils.LocalDateTimeAdapter;

@Entity
@Table(name = "qbaseentity_baseentity")
@RegisterForReflection
public class EntityEntity  extends PanacheEntity  implements java.io.Serializable, Comparable<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(EntityEntity.class);

    @EmbeddedId
    private EntityEntityId pk = new EntityEntityId();

    public EntityEntityId getPk() {
        return pk;
    }

    public void setPk(final EntityEntityId pk) {
        this.pk = pk;
    }

    public Link getLink() {
        return link;
    }


//	@AttributeOverrides({
//        @AttributeOverride(name = "sourceCode", column = @Column(name = "SOURCE_CODE", nullable = false)),
//        @AttributeOverride(name = "targetCode", column = @Column(name = "TARGET_CODE", nullable = false)),
//        @AttributeOverride(name = "attributeCode", column = @Column(name = "LINK_CODE", nullable = false)),
//        @AttributeOverride(name = "weight", column = @Column(name = "LINK_WEIGHT", nullable = false)),
//        @AttributeOverride(name = "parentColour", column = @Column(name = "PARENT_COL", nullable = true)),
//        @AttributeOverride(name = "childColour", column = @Column(name = "CHILD_COL", nullable = true)),
//        @AttributeOverride(name = "rule", column = @Column(name = "RULE", nullable = true))
//        
//	})

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
////
////	@JsonbTypeAdapter(AttributeAdapter.class)
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	public Attribute attribute;

	@JsonbTransient
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_ID", nullable = true)
	public BaseEntity source;

	@JsonbTransient
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TARGET_ID", nullable = true)
	public BaseEntity target;

    /**
     * Store the String value of the attribute for the baseEntity
     */
    @Column(name = "valueString", insertable = false, updatable = false)
    public String valueString;
    // For compatibility initially

    public String attributeCode;
    public String sourceCode;
    public String targetCode;

    @Embedded
    @NotNull
    public Value value = new Value();

    @Embedded
    public Link link = new Link();
//
//
//
//
//  /**
//   * Store the relative importance of the attribute for the baseEntity
//   */
//
//  public Double weight;
//
//  public Long version = 1L;
//

  public EntityEntity() {}

  /**
   * Constructor.
   * 
   * @param source the source baseEntity
   * @param target the target entity that is linked to
   * @param linkAttribute the associated linkAttribute
   * @param Weight the weighted importance of this attribute (relative to the other attributes)
   */
  public EntityEntity(final BaseEntity source, final BaseEntity target,
      final Attribute attribute, Double weight) {
   this(source,target,attribute, "DUMMY",weight);
  }

  /**
   * Constructor.
   * 
   * @param source the source baseEntity
   * @param target the target entity that is linked to
   * @param linkAttribute the associated linkAttribute
   * @param linkValue the associated linkValue
   * @param Weight the weighted importance of this attribute (relative to the other attributes)
   */
  public EntityEntity(final BaseEntity source, final BaseEntity target,
      final Attribute attribute, final Object value, Double weight) {
    this.source = source;
    this.attribute = attribute;
    this.realm = target.realm;
    this.sourceCode = source.code;
    this.targetCode = target.code;
    this.attributeCode = attribute.code;
    this.target = target;
    link = new Link(source.code,target.code,attribute.code,null);

    if (value != null) {
        this.value.setValue(value);
       }
    if (weight == null) {
      weight = 0.0; // This permits ease of adding attributes and hides
                    // attribute from scoring.
    }
    setWeight(weight);
  }
//
	/**
	 * Constructor.
	 * 
	 * @param BaseEntity    the entity that needs to contain attributes
	 * @param Attribute     the associated Attribute
	 * @param linkAttribute the associated linkAttribute
	 * @param Weight        the weighted importance of this attribute (relative to
	 *                      the other attributes)
	 * @param Value         the value associated with this attribute
	 */
	public EntityEntity(final BaseEntity source, final BaseEntity target, final Attribute attribute, Double weight,
			final Object value) {
		this(source, target, attribute, value, weight);
	}
//
// 
//
// 
  /**
   * @return the weight
   */
  public Double getWeight() {
    return link.weight;
  }

  /**
   * @param weight the weight to set
   */
  public void setWeight(final Double weight) {
    this.link.weight = weight;
  }





/**
 * @param link the link to set
 */
public void setLink(Link link) {
	this.link = link;
}
//@PreUpdate
//public void autocreateUpdate() {
//	updated = LocalDateTime.now(ZoneId.of("UTC"));
//}
//
//@PrePersist
//public void autocreateCreated() {
//	if (created == null)
//		created = LocalDateTime.now(ZoneId.of("UTC"));
//}
//
//@Transient
//@JsonbTransient
//public Date getCreatedDate() {
//	final Date out = Date.from(created.atZone(ZoneId.systemDefault()).toInstant());
//	return out;
//}
//
//@Transient
//@JsonbTransient
//public Date getUpdatedDate() {
//	if (updated == null)
//		return null;
//	final Date out = Date.from(updated.atZone(ZoneId.systemDefault()).toInstant());
//	return out;
//}
//
@SuppressWarnings("unchecked")
@JsonbTransient
@Transient
public <T> T getValue() {
	return value.getValue();

}
//
//
@JsonbTransient
@Transient
public <T> void setValue(final Object value) {

	if (value == null) {
		this.value.setValue(this.attribute.defaultValue);
	} else {
		this.value.setValue(value);
	}

}

@JsonbTransient
@Transient
public <T> void setLoopValue(final Object value) {
	setValue(value);
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

public int compareTo(EntityEntity obj) {
	if (this == obj)
		return 0;

	return value.compareTo(obj.value);
}
//
//
//
//@SuppressWarnings("unchecked")
//@JsonbTransient
//@Transient
//public <T> T getObject() {
//
//	return getValue();
//}
//
//@JsonbTransient
//@Transient
//public String getObjectAsString() {
//
//	return value.toString();
//
//}
//
//@JsonbTransient
//@Transient
//public Boolean getValueBoolean() {
//	return value.valueBoolean;
//}
//
//@JsonbTransient
//@Transient
//public String getValueString() {
//	return value.valueString;
//}
//
//@JsonbTransient
//@Transient
//public Double getValueDouble() {
//	return value.valueDouble;
//}
//
//@JsonbTransient
//@Transient
//public Integer getValueInteger() {
//	return value.valueInteger;
//}
//
//@JsonbTransient
//@Transient
//public Long getValueLong() {
//	return value.valueLong;
//}
//
//@JsonbTransient
//@Transient
//public LocalDateTime getValueDateTime() {
//	return value.valueDateTime;
//}
//
//@JsonbTransient
//@Transient
//public LocalDate getValueDate() {
//	return value.valueDate;
//}
//
//@JsonbTransient
//@Transient
//public LocalTime getValueTime() {
//	return value.valueTime;
//}

@Override
public int compareTo(Object o) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public int hashCode() {
  final int prime = 31;
  int result = 1;
//  result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
  result = prime * result + ((attributeCode == null) ? 0 : attributeCode.hashCode());
  result = prime * result + ((created == null) ? 0 : created.hashCode());
  result = prime * result + ((link == null) ? 0 : link.hashCode());
  result = prime * result + ((realm == null) ? 0 : realm.hashCode());
//  result = prime * result + ((source == null) ? 0 : source.hashCode());
  result = prime * result + ((sourceCode == null) ? 0 : sourceCode.hashCode());
//  result = prime * result + ((target == null) ? 0 : target.hashCode());
  result = prime * result + ((targetCode == null) ? 0 : targetCode.hashCode());
  result = prime * result + ((updated == null) ? 0 : updated.hashCode());
  result = prime * result + ((value == null) ? 0 : value.hashCode());
  return result;
}

@Override
public boolean equals(Object obj) {
  if (this == obj)
  {
    return true;
  }
  if (obj == null)
  {
    return false;
  }
  if (getClass() != obj.getClass())
  {
    return false;
  }
  EntityEntity other = (EntityEntity) obj;
  if (attribute == null) {
    if (other.attribute != null)
    {
      return false;
    }
  } else if (!attribute.equals(other.attribute))
  {
    return false;
  }
  if (attributeCode == null) {
    if (other.attributeCode != null)
    {
      return false;
    }
  } else if (!attributeCode.equals(other.attributeCode))
  {
    return false;
  }
  if (created == null) {
    if (other.created != null)
    {
      return false;
    }
  } else if (!created.equals(other.created))
  {
    return false;
  }
  if (link == null) {
    if (other.link != null)
    {
      return false;
    }
  } else if (!link.equals(other.link))
  {
    return false;
  }
  if (realm == null) {
    if (other.realm != null)
    {
      return false;
    }
  } else if (!realm.equals(other.realm))
  {
    return false;
  }
  if (source == null) {
    if (other.source != null)
    {
      return false;
    }
  } else if (!source.equals(other.source))
  {
    return false;
  }
  if (sourceCode == null) {
    if (other.sourceCode != null)
    {
      return false;
    }
  } else if (!sourceCode.equals(other.sourceCode))
  {
    return false;
  }
  if (target == null) {
    if (other.target != null)
    {
      return false;
    }
  } else if (!target.equals(other.target))
  {
    return false;
  }
  if (targetCode == null) {
    if (other.targetCode != null)
    {
      return false;
    }
  } else if (!targetCode.equals(other.targetCode))
  {
    return false;
  }
  if (updated == null) {
    if (other.updated != null)
    {
      return false;
    }
  } else if (!updated.equals(other.updated))
  {
    return false;
  }
  if (value == null) {
    if (other.value != null)
    {
      return false;
    }
  } else if (!value.equals(other.value))
  {
    return false;
  }
  return true;
}

@Override
public String toString() {
  return "EntityEntity [attribute=" + attribute + ", attributeCode=" + attributeCode + ", created=" + created
      + ", link=" + link + ", realm=" + realm + ", source=" + source + ", sourceCode=" + sourceCode + ", target="
      + target + ", targetCode=" + targetCode + ", updated=" + updated + ", value=" + value + "]";
}

}
